package com.acme.orders.invoice.infrastructure.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventEntity, UUID> {
  @Modifying
  @Query("delete from ProcessedEventEntity e where e.expiresAt < ?1")
  int deleteExpired(Instant now);
}
