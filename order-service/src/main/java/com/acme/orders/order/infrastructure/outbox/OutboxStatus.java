package com.acme.orders.order.infrastructure.outbox;

public enum OutboxStatus {
  PENDING,
  PUBLISHED,
  FAILED
}
