package com.acme.orders.order.infrastructure.persistence;

import com.acme.orders.order.application.OrderRepository;
import com.acme.orders.order.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {
  private final OrderJpaRepository repository;

  public OrderRepositoryAdapter(OrderJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Order save(Order order) {
    OrderEntity saved = repository.save(OrderMapper.toEntity(order));
    return OrderMapper.toDomain(saved);
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return repository.findById(id).map(OrderMapper::toDomain);
  }
}
