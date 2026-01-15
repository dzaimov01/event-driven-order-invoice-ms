package com.acme.orders.order.domain;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmed(UUID eventId, UUID orderId, Instant occurredAt) implements DomainEvent {
  public static OrderConfirmed now(UUID orderId) {
    return new OrderConfirmed(UUID.randomUUID(), orderId, Instant.now());
  }
}
