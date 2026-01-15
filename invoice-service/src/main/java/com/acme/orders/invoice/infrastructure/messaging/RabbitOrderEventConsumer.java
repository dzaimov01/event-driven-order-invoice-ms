package com.acme.orders.invoice.infrastructure.messaging;

import com.acme.orders.invoice.application.OrderEventHandler;
import com.acme.orders.invoice.application.OrderEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.mode", havingValue = "rabbitmq")
public class RabbitOrderEventConsumer {
  private static final Logger log = LoggerFactory.getLogger(RabbitOrderEventConsumer.class);

  private final ObjectMapper objectMapper;
  private final OrderEventHandler handler;

  public RabbitOrderEventConsumer(ObjectMapper objectMapper, OrderEventHandler handler) {
    this.objectMapper = objectMapper;
    this.handler = handler;
  }

  @RabbitListener(queues = "${app.messaging.order-queue:order.events}")
  public void consume(String payload) {
    try {
      OrderEventPayload event = objectMapper.readValue(payload, OrderEventPayload.class);
      handler.handle(event);
      log.info("order_event_processed_rabbit eventId={} orderId={} type={}",
          event.eventId(), event.orderId(), event.type());
    } catch (Exception ex) {
      log.error("order_event_failed_rabbit payload={} error={}", payload, ex.getMessage());
      throw new IllegalStateException("Failed to process order event", ex);
    }
  }
}
