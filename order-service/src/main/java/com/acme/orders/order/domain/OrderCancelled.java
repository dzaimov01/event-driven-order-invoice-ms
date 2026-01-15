package com.acme.orders.order.domain;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelled(UUID eventId, UUID orderId, Instant occurredAt) implements DomainEvent {
  public static OrderCancelled now(UUID orderId) {
    return new OrderCancelled(UUID.randomUUID(), orderId, Instant.now());
  }
}
