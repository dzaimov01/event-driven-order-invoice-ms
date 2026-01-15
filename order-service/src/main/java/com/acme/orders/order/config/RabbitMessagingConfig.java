package com.acme.orders.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.messaging.mode", havingValue = "rabbitmq")
public class RabbitMessagingConfig {

  @Bean
  public DirectExchange orderExchange(@Value("${app.messaging.order-exchange:order.events}") String exchangeName) {
    return new DirectExchange(exchangeName);
  }

  @Bean
  public Queue orderQueue(@Value("${app.messaging.order-queue:order.events}") String queueName) {
    return new Queue(queueName, true);
  }

  @Bean
  public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange,
                              @Value("${app.messaging.order-routing-key:order.events}") String routingKey) {
    return BindingBuilder.bind(orderQueue).to(orderExchange).with(routingKey);
  }
}
