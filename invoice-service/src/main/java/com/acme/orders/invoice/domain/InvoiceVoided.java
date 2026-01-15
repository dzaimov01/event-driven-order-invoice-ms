package com.acme.orders.invoice.domain;

import java.time.Instant;
import java.util.UUID;

public record InvoiceVoided(UUID eventId, UUID invoiceId, UUID orderId, Instant occurredAt) implements DomainEvent {
  public static InvoiceVoided now(UUID invoiceId, UUID orderId) {
    return new InvoiceVoided(UUID.randomUUID(), invoiceId, orderId, Instant.now());
  }
}
