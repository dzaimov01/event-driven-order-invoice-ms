package com.acme.orders.order.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderLineRequest(
    @NotNull UUID productId,
    @Min(1) int quantity,
    @NotNull BigDecimal unitPrice,
    @NotBlank String currency
) {
}
