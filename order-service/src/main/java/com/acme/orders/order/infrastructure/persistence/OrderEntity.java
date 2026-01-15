package com.acme.orders.order.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
  @Id
  private UUID id;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatusEntity status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderLineEntity> lines = new ArrayList<>();

  protected OrderEntity() {
  }

  public OrderEntity(UUID id, UUID customerId, OrderStatusEntity status, Instant createdAt) {
    this.id = id;
    this.customerId = customerId;
    this.status = status;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public OrderStatusEntity getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public List<OrderLineEntity> getLines() {
    return lines;
  }

  public void setLines(List<OrderLineEntity> lines) {
    this.lines = lines;
  }

  public void setStatus(OrderStatusEntity status) {
    this.status = status;
  }
}
