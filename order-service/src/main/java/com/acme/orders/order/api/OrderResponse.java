package com.acme.orders.order.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID orderId,
    UUID customerId,
    String status,
    String currency,
    String total,
    Instant createdAt,
    List<OrderLineResponse> lines
) {
}
