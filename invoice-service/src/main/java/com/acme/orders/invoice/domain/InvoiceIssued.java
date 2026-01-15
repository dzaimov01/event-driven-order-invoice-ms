package com.acme.orders.invoice.domain;

import java.time.Instant;
import java.util.UUID;

public record InvoiceIssued(UUID eventId, UUID invoiceId, UUID orderId, Instant occurredAt) implements DomainEvent {
  public static InvoiceIssued now(UUID invoiceId, UUID orderId) {
    return new InvoiceIssued(UUID.randomUUID(), invoiceId, orderId, Instant.now());
  }
}
