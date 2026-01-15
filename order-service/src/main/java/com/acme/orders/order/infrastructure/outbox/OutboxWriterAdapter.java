package com.acme.orders.order.infrastructure.outbox;

import com.acme.orders.order.application.OutboxWriter;
import com.acme.orders.order.domain.DomainEvent;
import com.acme.orders.order.domain.Order;
import com.acme.orders.order.domain.OrderLine;
import com.acme.orders.order.domain.OrderCancelled;
import com.acme.orders.order.domain.OrderConfirmed;
import com.acme.orders.order.domain.OrderCreated;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class OutboxWriterAdapter implements OutboxWriter {
  private final OutboxJpaRepository repository;
  private final ObjectMapper objectMapper;

  public OutboxWriterAdapter(OutboxJpaRepository repository, ObjectMapper objectMapper) {
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void write(Order order, DomainEvent event, String aggregateType, String aggregateId) {
    String payload = serialize(order, event);
    OutboxMessage message = new OutboxMessage(event.eventId(), aggregateType, aggregateId,
        event.getClass().getSimpleName(), payload, OutboxStatus.PENDING, Instant.now(), 0);
    repository.save(message);
  }

  private String serialize(Order order, DomainEvent event) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("eventId", event.eventId());
    payload.put("occurredAt", event.occurredAt());
    payload.put("version", "v1");
    payload.put("orderId", order.id());
    payload.put("customerId", order.customerId().value());
    payload.put("status", order.status().name());
    payload.put("total", order.total().amount().toPlainString());
    payload.put("currency", order.total().currency());
    payload.put("lines", order.lines().stream().map(this::linePayload).toList());
    String correlationId = MDC.get("correlationId");
    if (correlationId != null) {
      payload.put("correlationId", correlationId);
    }

    if (event instanceof OrderCreated created) {
      payload.put("type", "OrderCreated");
    } else if (event instanceof OrderConfirmed confirmed) {
      payload.put("type", "OrderConfirmed");
    } else if (event instanceof OrderCancelled cancelled) {
      payload.put("type", "OrderCancelled");
    } else {
      throw new IllegalArgumentException("Unsupported event type");
    }

    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException ex) {
      throw new IllegalStateException("Failed to serialize outbox payload", ex);
    }
  }

  private Map<String, Object> linePayload(OrderLine line) {
    Map<String, Object> map = new HashMap<>();
    map.put("productId", line.productId());
    map.put("quantity", line.quantity());
    map.put("unitPrice", line.unitPrice().amount().toPlainString());
    map.put("currency", line.unitPrice().currency());
    return map;
  }
}
