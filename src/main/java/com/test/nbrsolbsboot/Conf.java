// Copyright (c) 2020 Quirino Gervacio

package com.test.nbrsolbsboot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class Conf {

    @Value("${app.queue.target}")
    private String[] queues;

    @Value("${app.queue.header.retry}")
    private String qHeaderRetry;

    @Value("${app.queue.postfix.park}")
    private String qPostfixPark;

    @Value("${app.retry.timings}")
    private int[] retryTimings;
}
