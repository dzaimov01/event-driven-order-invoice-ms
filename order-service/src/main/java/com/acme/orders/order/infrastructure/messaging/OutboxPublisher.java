package com.acme.orders.order.infrastructure.messaging;

import com.acme.orders.order.infrastructure.outbox.OutboxJpaRepository;
import com.acme.orders.order.infrastructure.outbox.OutboxMessage;
import com.acme.orders.order.infrastructure.outbox.OutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.MDC;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.time.Instant;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "app.messaging.mode", havingValue = "kafka", matchIfMissing = true)
public class OutboxPublisher {
  private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

  private final OutboxJpaRepository outboxRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topic;

  public OutboxPublisher(OutboxJpaRepository outboxRepository,
                         KafkaTemplate<String, String> kafkaTemplate,
                         @Value("${app.messaging.order-topic}") String topic) {
    this.outboxRepository = outboxRepository;
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
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
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message.getAggregateId(), message.getPayload());
        String correlationId = MDC.get("correlationId");
        if (correlationId != null) {
          record.headers().add(new RecordHeader("X-Correlation-Id", correlationId.getBytes(StandardCharsets.UTF_8)));
        }
        kafkaTemplate.send(record).get(5, TimeUnit.SECONDS);
        message.markPublished(Instant.now());
        log.info("outbox_published eventId={} aggregateId={} eventType={}",
            message.getId(), message.getAggregateId(), message.getEventType());
      } catch (Exception ex) {
        message.markFailed();
        log.warn("outbox_publish_failed eventId={} aggregateId={} retryCount={} error={}",
            message.getId(), message.getAggregateId(), message.getRetryCount(), ex.getMessage());
      }
    }
  }
}
