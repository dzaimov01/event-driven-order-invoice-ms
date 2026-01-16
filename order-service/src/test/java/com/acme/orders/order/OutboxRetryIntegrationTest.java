package com.acme.orders.order;

import com.acme.orders.order.application.OrderApplicationService;
import com.acme.orders.order.domain.Money;
import com.acme.orders.order.domain.OrderLine;
import com.acme.orders.order.infrastructure.messaging.OutboxPublisher;
import com.acme.orders.order.infrastructure.outbox.OutboxJpaRepository;
import com.acme.orders.order.infrastructure.outbox.OutboxMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class OutboxRetryIntegrationTest {

  @Container
  static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
      .withDatabaseName("orders")
      .withUsername("orders")
      .withPassword("orders");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9099");
  }

  @Autowired
  private OrderApplicationService orderApplicationService;

  @Autowired
  private OutboxJpaRepository outboxRepository;

  @Autowired
  private OutboxPublisher outboxPublisher;

  @Test
  void outboxRetriesWhenBrokerUnavailable() throws Exception {
    orderApplicationService.createOrder(UUID.randomUUID(), List.of(sampleLine()));

    boolean retryObserved = waitForRetry();
    assertTrue(retryObserved, "Expected retry count to increase");
  }

  private boolean waitForRetry() throws InterruptedException {
    long deadline = System.currentTimeMillis() + Duration.ofSeconds(10).toMillis();
    while (System.currentTimeMillis() < deadline) {
      outboxPublisher.publishPending();
      List<OutboxMessage> all = outboxRepository.findAll();
      if (!all.isEmpty() && all.get(0).getRetryCount() > 0) {
        return true;
      }
      Thread.sleep(500);
    }
    return false;
  }

  private OrderLine sampleLine() {
    return new OrderLine(UUID.randomUUID(), 1, new Money(new BigDecimal("10.00"), "USD"));
  }
}
