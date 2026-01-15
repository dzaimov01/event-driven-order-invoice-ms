package com.acme.orders.order.domain;

import java.util.Objects;
import java.util.UUID;

public record OrderLine(UUID productId, int quantity, Money unitPrice) {
  public OrderLine {
    Objects.requireNonNull(productId, "productId");
    Objects.requireNonNull(unitPrice, "unitPrice");
    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity must be positive");
    }
  }

  public Money lineTotal() {
    return new Money(unitPrice.amount().multiply(java.math.BigDecimal.valueOf(quantity)), unitPrice.currency());
  }
}
