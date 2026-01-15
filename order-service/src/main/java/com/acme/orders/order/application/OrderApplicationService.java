package com.acme.orders.order.application;

import com.acme.orders.order.domain.CustomerId;
import com.acme.orders.order.domain.Order;
import com.acme.orders.order.domain.OrderCancelled;
import com.acme.orders.order.domain.OrderConfirmed;
import com.acme.orders.order.domain.OrderCreated;
import com.acme.orders.order.domain.OrderLine;
import com.acme.orders.order.domain.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderApplicationService {
  private static final Logger log = LoggerFactory.getLogger(OrderApplicationService.class);
  private final OrderRepository orderRepository;
  private final OutboxWriter outboxWriter;

  public OrderApplicationService(OrderRepository orderRepository, OutboxWriter outboxWriter) {
    this.orderRepository = orderRepository;
    this.outboxWriter = outboxWriter;
  }

  @Transactional
  public Order createOrder(UUID customerId, List<OrderLine> lines) {
    Order order = Order.createNew(new CustomerId(customerId), lines);
    Order saved = orderRepository.save(order);
    outboxWriter.write(saved, OrderCreated.now(saved.id()), "Order", saved.id().toString());
    log.info("order_created orderId={} status={}", saved.id(), saved.status());
    return saved;
  }

  @Transactional
  public Order confirmOrder(UUID orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    order.confirm();
    Order saved = orderRepository.save(order);
    outboxWriter.write(saved, OrderConfirmed.now(saved.id()), "Order", saved.id().toString());
    log.info("order_confirmed orderId={} status={}", saved.id(), saved.status());
    return saved;
  }

  @Transactional
  public Order cancelOrder(UUID orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    if (order.status() == OrderStatus.CONFIRMED) {
      throw new IllegalStateException("Confirmed orders cannot be cancelled");
    }
    order.cancel();
    Order saved = orderRepository.save(order);
    outboxWriter.write(saved, OrderCancelled.now(saved.id()), "Order", saved.id().toString());
    log.info("order_cancelled orderId={} status={}", saved.id(), saved.status());
    return saved;
  }
}
