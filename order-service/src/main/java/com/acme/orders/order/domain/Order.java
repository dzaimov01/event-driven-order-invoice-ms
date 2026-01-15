package com.acme.orders.order.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Order {
  private final UUID id;
  private final CustomerId customerId;
  private final List<OrderLine> lines;
  private OrderStatus status;
  private final Instant createdAt;

  public Order(UUID id, CustomerId customerId, List<OrderLine> lines, OrderStatus status, Instant createdAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.customerId = Objects.requireNonNull(customerId, "customerId");
    this.lines = List.copyOf(Objects.requireNonNull(lines, "lines"));
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("order must have at least one line");
    }
    this.status = Objects.requireNonNull(status, "status");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
  }

  public static Order createNew(CustomerId customerId, List<OrderLine> lines) {
    return new Order(UUID.randomUUID(), customerId, lines, OrderStatus.CREATED, Instant.now());
  }

  public void confirm() {
    if (status == OrderStatus.CANCELLED) {
      throw new IllegalStateException("cannot confirm a cancelled order");
    }
    this.status = OrderStatus.CONFIRMED;
  }

  public void cancel() {
    if (status == OrderStatus.CONFIRMED) {
      throw new IllegalStateException("cannot cancel a confirmed order");
    }
    this.status = OrderStatus.CANCELLED;
  }

  public Money total() {
    Money total = Money.zero(lines.get(0).unitPrice().currency());
    for (OrderLine line : lines) {
      total = total.add(line.lineTotal());
    }
    return total;
  }

  public UUID id() {
    return id;
  }

  public CustomerId customerId() {
    return customerId;
  }

  public List<OrderLine> lines() {
    return lines;
  }

  public OrderStatus status() {
    return status;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
