package com.acme.orders.invoice.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Invoice {
  private final UUID id;
  private final UUID orderId;
  private final List<InvoiceLine> lines;
  private InvoiceStatus status;
  private final Instant issuedAt;

  public Invoice(UUID id, UUID orderId, List<InvoiceLine> lines, InvoiceStatus status, Instant issuedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.orderId = Objects.requireNonNull(orderId, "orderId");
    this.lines = List.copyOf(Objects.requireNonNull(lines, "lines"));
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("invoice must have at least one line");
    }
    this.status = Objects.requireNonNull(status, "status");
    this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt");
  }

  public static Invoice issue(UUID orderId, List<InvoiceLine> lines) {
    return new Invoice(UUID.randomUUID(), orderId, lines, InvoiceStatus.ISSUED, Instant.now());
  }

  public void voidInvoice() {
    this.status = InvoiceStatus.VOIDED;
  }

  public UUID id() {
    return id;
  }

  public UUID orderId() {
    return orderId;
  }

  public List<InvoiceLine> lines() {
    return lines;
  }

  public InvoiceStatus status() {
    return status;
  }

  public Instant issuedAt() {
    return issuedAt;
  }
}
