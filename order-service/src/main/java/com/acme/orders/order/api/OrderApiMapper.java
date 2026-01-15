package com.acme.orders.order.api;

import com.acme.orders.order.domain.Money;
import com.acme.orders.order.domain.Order;
import com.acme.orders.order.domain.OrderLine;

import java.util.List;

public final class OrderApiMapper {
  private OrderApiMapper() {
  }

  public static OrderLine toDomain(OrderLineRequest request) {
    return new OrderLine(request.productId(), request.quantity(), new Money(request.unitPrice(), request.currency()));
  }

  public static OrderResponse toResponse(Order order) {
    List<OrderLineResponse> lines = order.lines().stream()
        .map(line -> new OrderLineResponse(line.productId(), line.quantity(),
            line.unitPrice().amount().toPlainString(), line.unitPrice().currency()))
        .toList();
    return new OrderResponse(order.id(), order.customerId().value(), order.status().name(),
        order.total().currency(), order.total().amount().toPlainString(), order.createdAt(), lines);
  }
}
