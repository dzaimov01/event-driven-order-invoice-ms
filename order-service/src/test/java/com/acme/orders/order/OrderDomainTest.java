package com.acme.orders.order;

import com.acme.orders.order.domain.CustomerId;
import com.acme.orders.order.domain.Money;
import com.acme.orders.order.domain.Order;
import com.acme.orders.order.domain.OrderLine;
import com.acme.orders.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderDomainTest {

  @Test
  void createOrderRequiresLines() {
    assertThrows(IllegalArgumentException.class,
        () -> new Order(UUID.randomUUID(), new CustomerId(UUID.randomUUID()), List.of(), OrderStatus.CREATED, java.time.Instant.now()));
  }

  @Test
  void cancelConfirmedOrderIsRejected() {
    Order order = Order.createNew(new CustomerId(UUID.randomUUID()), List.of(sampleLine()));
    order.confirm();
    assertThrows(IllegalStateException.class, order::cancel);
  }

  @Test
  void totalAggregatesLines() {
    Order order = Order.createNew(new CustomerId(UUID.randomUUID()), List.of(
        new OrderLine(UUID.randomUUID(), 2, new Money(new BigDecimal("10.00"), "USD")),
        new OrderLine(UUID.randomUUID(), 1, new Money(new BigDecimal("5.00"), "USD"))
    ));

    assertEquals("25.00", order.total().amount().toPlainString());
  }

  private OrderLine sampleLine() {
    return new OrderLine(UUID.randomUUID(), 1, new Money(new BigDecimal("9.99"), "USD"));
  }
}
