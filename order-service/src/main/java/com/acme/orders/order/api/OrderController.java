package com.acme.orders.order.api;

import com.acme.orders.order.application.OrderApplicationService;
import com.acme.orders.order.application.OrderQueryService;
import com.acme.orders.order.domain.Order;
import com.acme.orders.order.domain.OrderLine;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
  private final OrderApplicationService orderApplicationService;
  private final OrderQueryService orderQueryService;

  public OrderController(OrderApplicationService orderApplicationService, OrderQueryService orderQueryService) {
    this.orderApplicationService = orderApplicationService;
    this.orderQueryService = orderQueryService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
    List<OrderLine> lines = request.lines().stream().map(OrderApiMapper::toDomain).toList();
    Order order = orderApplicationService.createOrder(request.customerId(), lines);
    return OrderApiMapper.toResponse(order);
  }

  @PostMapping("/{id}/confirm")
  public OrderResponse confirm(@PathVariable("id") UUID id) {
    Order order = orderApplicationService.confirmOrder(id);
    return OrderApiMapper.toResponse(order);
  }

  @PostMapping("/{id}/cancel")
  public OrderResponse cancel(@PathVariable("id") UUID id) {
    Order order = orderApplicationService.cancelOrder(id);
    return OrderApiMapper.toResponse(order);
  }

  @GetMapping("/{id}")
  public OrderResponse get(@PathVariable("id") UUID id) {
    Order order = orderQueryService.getById(id);
    return OrderApiMapper.toResponse(order);
  }
}
