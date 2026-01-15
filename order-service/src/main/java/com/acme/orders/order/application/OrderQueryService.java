package com.acme.orders.order.application;

import com.acme.orders.order.domain.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderQueryService {
  private final OrderRepository orderRepository;

  public OrderQueryService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Transactional(readOnly = true)
  public Order getById(UUID orderId) {
    return orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
  }
}
