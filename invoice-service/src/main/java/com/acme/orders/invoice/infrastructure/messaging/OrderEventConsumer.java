package com.acme.orders.invoice.infrastructure.messaging;

import com.acme.orders.invoice.application.OrderEventHandler;
import com.acme.orders.invoice.application.OrderEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.mode", havingValue = "kafka", matchIfMissing = true)
public class OrderEventConsumer {
  private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

  private final ObjectMapper objectMapper;
  private final OrderEventHandler handler;

  public OrderEventConsumer(ObjectMapper objectMapper, OrderEventHandler handler) {
    this.objectMapper = objectMapper;
    this.handler = handler;
  }

  @KafkaListener(topics = "${app.messaging.order-topic}", groupId = "invoice-service")
  public void consume(String payload, @Header(name = "X-Correlation-Id", required = false) String correlationId) {
    try {
      OrderEventPayload event = objectMapper.readValue(payload, OrderEventPayload.class);
      handler.handle(event);
      log.info("order_event_processed eventId={} orderId={} type={} correlationId={}",
          event.eventId(), event.orderId(), event.type(), correlationId);
    } catch (Exception ex) {
      log.error("order_event_failed payload={} error={}", payload, ex.getMessage());
      throw new IllegalStateException("Failed to process order event", ex);
    }
  }
}
