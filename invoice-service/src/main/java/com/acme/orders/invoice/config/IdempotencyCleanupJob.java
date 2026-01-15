package com.acme.orders.invoice.config;

import com.acme.orders.invoice.application.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class IdempotencyCleanupJob {
  private static final Logger log = LoggerFactory.getLogger(IdempotencyCleanupJob.class);

  private final ProcessedEventRepository repository;

  public IdempotencyCleanupJob(ProcessedEventRepository repository) {
    this.repository = repository;
  }

  @Scheduled(cron = "${app.idempotency.cleanup-cron:0 0 3 * * *}")
  @Transactional
  public void cleanup() {
    int deleted = repository.deleteExpired(Instant.now());
    if (deleted > 0) {
      log.info("idempotency_cleanup deleted={}", deleted);
    }
  }
}
