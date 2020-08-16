// Copyright (c) 2020 Quirino Gervacio

package com.test.nbrsolbsboot;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class Listener {

    private final Conf conf;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {"#{conf.getQueues()}"})
    void entry(final Message message, final Channel channel) {

        final String payload = new String(message.getBody());
        final MessageProperties prop = message.getMessageProperties();
        final String sourceQ = prop.getConsumerQueue();

        try {
            // do something with the message
            final com.test.nbrsolbsboot.Message model = new Gson().fromJson(
                payload, com.test.nbrsolbsboot.Message.class);
            log.info("Got me a {}", model);
        }

        // normally for malformed input, we would want to just give up and
        // move the message to park queue. but for the sake for example
        // we will ignore this utter negligence.
        /*catch (final JsonSyntaxException ex) {
            log.warn("Giving up on malformed input {} / {}", payload, ex.getMessage());
            this.rabbitTemplate.convertAndSend(
                sourceQ + this.conf.getQPostfixPark(),
                payload); // maybe add in the exception cause as well not just the payload.
        }*/

        // retries
        catch (final Exception ex) {
            log.warn("Failed to process message: {}", ex.getMessage());

            // increase retry counter. init if it doesn't exists
            final int limit = this.conf.getRetryTimings().length;
            final String counterV = prop.getHeader(this.conf.getQHeaderRetry());
            final Integer counter = NumberUtils.toInt(counterV, 0) + 1;
            prop.setHeader(this.conf.getQHeaderRetry(), counter + "");
            log.error("Attempt count {}/{} for failed message '{}' with exception '{}'",
                counter, limit, message, ex.getMessage());

            // TODO: maybe you would want to create a message for a notification queue here
            // so that your users wold be notified for failures?

            // park the message if max tries is reached
            if (counter == limit) {
                log.warn("Will now park since attempt limit reached for message {}", message);
                this.rabbitTemplate.convertAndSend(sourceQ + this.conf.getQPostfixPark(), payload);
            } else {
                // send the message to entry queue with delay
                final int delay = (this.conf.getRetryTimings()[counter]) * 60000;
                prop.setDelay(delay);
                log.warn("Sending back to {} with delay of {}ms", sourceQ, delay);
                this.rabbitTemplate.send(sourceQ, sourceQ, message);
            }
        }
    }
}