package com.acme.orders.order.infrastructure.messaging;

import com.acme.orders.order.infrastructure.outbox.OutboxJpaRepository;
import com.acme.orders.order.infrastructure.outbox.OutboxMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.messaging.mode", havingValue = "rabbitmq")
public class RabbitOutboxPublisher {
  private static final Logger log = LoggerFactory.getLogger(RabbitOutboxPublisher.class);

  private final OutboxJpaRepository outboxRepository;
  private final RabbitTemplate rabbitTemplate;
  private final String exchange;
  private final String routingKey;

  public RabbitOutboxPublisher(OutboxJpaRepository outboxRepository,
                               RabbitTemplate rabbitTemplate,
                               @Value("${app.messaging.order-exchange:order.events}") String exchange,
                               @Value("${app.messaging.order-routing-key:order.events}") String routingKey) {
    this.outboxRepository = outboxRepository;
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    this.routingKey = routingKey;
  }

  @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:1000}")
  @Transactional
  public void publishPending() {
    List<OutboxMessage> pending = outboxRepository.findPendingLocked();
    if (pending.isEmpty()) {
      return;
    }

    for (OutboxMessage message : pending) {
      try {
        rabbitTemplate.convertAndSend(exchange, routingKey, message.getPayload());
        message.markPublished(Instant.now());
        log.info("outbox_published_rabbit eventId={} aggregateId={} eventType={}",
            message.getId(), message.getAggregateId(), message.getEventType());
      } catch (Exception ex) {
        message.markFailed();
        log.warn("outbox_publish_failed_rabbit eventId={} aggregateId={} retryCount={} error={}",
            message.getId(), message.getAggregateId(), message.getRetryCount(), ex.getMessage());
      }
    }
  }
}
