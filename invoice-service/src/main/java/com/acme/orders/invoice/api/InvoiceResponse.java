package com.acme.orders.invoice.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
    UUID invoiceId,
    UUID orderId,
    String status,
    Instant issuedAt,
    List<InvoiceLineResponse> lines
) {
}
