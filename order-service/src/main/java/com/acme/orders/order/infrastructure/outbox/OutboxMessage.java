package com.acme.orders.order.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox")
public class OutboxMessage {
  @Id
  private UUID id;

  @Column(name = "aggregate_type", nullable = false)
  private String aggregateType;

  @Column(name = "aggregate_id", nullable = false)
  private String aggregateId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
  private String payload;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OutboxStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "published_at")
  private Instant publishedAt;

  @Column(name = "retry_count", nullable = false)
  private int retryCount;

  protected OutboxMessage() {
  }

  public OutboxMessage(UUID id, String aggregateType, String aggregateId, String eventType, String payload,
                       OutboxStatus status, Instant createdAt, int retryCount) {
    this.id = id;
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.payload = payload;
    this.status = status;
    this.createdAt = createdAt;
    this.retryCount = retryCount;
  }

  public UUID getId() {
    return id;
  }

  public String getAggregateType() {
    return aggregateType;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public String getEventType() {
    return eventType;
  }

  public String getPayload() {
    return payload;
  }

  public OutboxStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public int getRetryCount() {
    return retryCount;
  }

  public void markPublished(Instant publishedAt) {
    this.status = OutboxStatus.PUBLISHED;
    this.publishedAt = publishedAt;
  }

  public void markFailed() {
    this.status = OutboxStatus.FAILED;
    this.retryCount += 1;
  }
}
