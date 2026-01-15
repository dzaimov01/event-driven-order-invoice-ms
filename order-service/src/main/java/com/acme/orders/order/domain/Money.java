package com.acme.orders.order.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {
  public Money {
    Objects.requireNonNull(amount, "amount");
    Objects.requireNonNull(currency, "currency");
    if (amount.scale() > 2) {
      throw new IllegalArgumentException("amount scale must be <= 2");
    }
    if (amount.signum() < 0) {
      throw new IllegalArgumentException("amount must be non-negative");
    }
  }

  public static Money zero(String currency) {
    return new Money(BigDecimal.ZERO, currency);
  }

  public Money add(Money other) {
    if (!currency.equals(other.currency)) {
      throw new IllegalArgumentException("currency mismatch");
    }
    return new Money(amount.add(other.amount), currency);
  }
}
