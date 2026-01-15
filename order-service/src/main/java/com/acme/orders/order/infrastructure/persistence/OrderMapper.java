package com.acme.orders.order.infrastructure.persistence;

import com.acme.orders.order.domain.CustomerId;
import com.acme.orders.order.domain.Money;
import com.acme.orders.order.domain.Order;
import com.acme.orders.order.domain.OrderLine;
import com.acme.orders.order.domain.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public final class OrderMapper {
  private OrderMapper() {
  }

  public static OrderEntity toEntity(Order order) {
    OrderEntity entity = new OrderEntity(order.id(), order.customerId().value(),
        OrderStatusEntity.valueOf(order.status().name()), order.createdAt());
    List<OrderLineEntity> lineEntities = new ArrayList<>();
    for (OrderLine line : order.lines()) {
      lineEntities.add(new OrderLineEntity(entity, line.productId(), line.quantity(),
          line.unitPrice().amount(), line.unitPrice().currency()));
    }
    entity.setLines(lineEntities);
    return entity;
  }

  public static Order toDomain(OrderEntity entity) {
    List<OrderLine> lines = new ArrayList<>();
    for (OrderLineEntity line : entity.getLines()) {
      lines.add(new OrderLine(line.getProductId(), line.getQuantity(),
          new Money(line.getUnitPrice(), line.getCurrency())));
    }
    return new Order(entity.getId(), new CustomerId(entity.getCustomerId()), lines,
        OrderStatus.valueOf(entity.getStatus().name()), entity.getCreatedAt());
  }
}
