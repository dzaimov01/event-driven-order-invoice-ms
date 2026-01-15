package com.acme.orders.invoice.infrastructure.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
public class ProcessedEventEntity {
  @Id
  private UUID eventId;

  @Column(name = "received_at", nullable = false)
  private Instant receivedAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  protected ProcessedEventEntity() {
  }

  public ProcessedEventEntity(UUID eventId, Instant receivedAt, Instant expiresAt) {
    this.eventId = eventId;
    this.receivedAt = receivedAt;
    this.expiresAt = expiresAt;
  }

  public UUID getEventId() {
    return eventId;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }
}
