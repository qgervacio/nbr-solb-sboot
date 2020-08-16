# RabbitMQ Non-Blocking Retry - Solution B - Springboot

This repository is to support my [Medium](https://medium.com/@qgervacio/rabbitmq-non-blocking-retry-solutions-in-springboot-fe589f69f367) article in implementing
a non-blocking retry solutions for RabbitMQ with Spring Framework
using a delay plugin.

## Design

You will need a total of 4 queues in order to utilize this solution.

| Queue Name     | Purpose                                                       | Sample            |
|----------------|---------------------------------------------------------------|-------------------|
| `*`            | entry queue                                                   | `test`            |
| `*-park`       | final destination of message after all attempts are exhausted | `test-park`       |

## References

1. [RabbitMQ - Non-Blocking Retry Solutions In SpringBoot](https://medium.com/@qgervacio/rabbitmq-non-blocking-retry-solutions-in-springboot-fe589f69f367)
2. [RabbitMQ Test](https://github.com/qgervacio/rabbitmq-test)
3. [Non-Blocking Retry Solution A - SpringBoot](https://github.com/qgervacio/nbr-sola-sboot)