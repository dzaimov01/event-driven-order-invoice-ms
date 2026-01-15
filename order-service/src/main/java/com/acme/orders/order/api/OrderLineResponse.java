package com.acme.orders.order.api;

import java.util.UUID;

public record OrderLineResponse(UUID productId, int quantity, String unitPrice, String currency) {
}
