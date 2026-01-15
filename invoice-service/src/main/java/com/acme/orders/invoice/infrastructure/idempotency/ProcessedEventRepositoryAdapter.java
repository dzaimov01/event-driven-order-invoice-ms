package com.acme.orders.invoice.infrastructure.idempotency;

import com.acme.orders.invoice.application.ProcessedEventRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepository {
  private final ProcessedEventJpaRepository repository;

  public ProcessedEventRepositoryAdapter(ProcessedEventJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean exists(UUID eventId) {
    return repository.existsById(eventId);
  }

  @Override
  public void record(UUID eventId, Instant expiresAt) {
    repository.save(new ProcessedEventEntity(eventId, Instant.now(), expiresAt));
  }

  @Override
  public int deleteExpired(Instant now) {
    return repository.deleteExpired(now);
  }
}
