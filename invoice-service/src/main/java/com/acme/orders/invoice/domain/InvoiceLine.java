package com.acme.orders.invoice.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record InvoiceLine(UUID productId, int quantity, BigDecimal unitPrice, String currency) {
  public InvoiceLine {
    Objects.requireNonNull(productId, "productId");
    Objects.requireNonNull(unitPrice, "unitPrice");
    Objects.requireNonNull(currency, "currency");
    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity must be positive");
    }
  }
}
