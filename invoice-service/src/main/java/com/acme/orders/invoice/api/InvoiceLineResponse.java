package com.acme.orders.invoice.api;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceLineResponse(UUID productId, int quantity, BigDecimal unitPrice, String currency) {
}
