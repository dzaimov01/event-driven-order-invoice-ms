package com.acme.orders.order.application;

import com.acme.orders.order.domain.DomainEvent;
import com.acme.orders.order.domain.Order;

public interface OutboxWriter {
  void write(Order order, DomainEvent event, String aggregateType, String aggregateId);
}
