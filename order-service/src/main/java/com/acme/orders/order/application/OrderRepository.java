package com.acme.orders.order.application;

import com.acme.orders.order.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
  Order save(Order order);
  Optional<Order> findById(UUID id);
}
