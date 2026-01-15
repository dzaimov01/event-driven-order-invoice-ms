package com.acme.orders.invoice.application;

import java.time.Instant;
import java.util.UUID;

public interface ProcessedEventRepository {
  boolean exists(UUID eventId);
  void record(UUID eventId, Instant expiresAt);
  int deleteExpired(Instant now);
}
