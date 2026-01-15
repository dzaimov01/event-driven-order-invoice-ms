package com.acme.orders.invoice.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderEventPayload(
    UUID eventId,
    String type,
    UUID orderId,
    UUID customerId,
    String status,
    String total,
    String currency,
    Instant occurredAt,
    List<OrderLinePayload> lines
) {
  public record OrderLinePayload(UUID productId, int quantity, BigDecimal unitPrice, String currency) {
  }
}
