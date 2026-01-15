package com.acme.orders.order.domain;

import java.time.Instant;
import java.util.UUID;

public record OrderCreated(UUID eventId, UUID orderId, Instant occurredAt) implements DomainEvent {
  public static OrderCreated now(UUID orderId) {
    return new OrderCreated(UUID.randomUUID(), orderId, Instant.now());
  }
}
