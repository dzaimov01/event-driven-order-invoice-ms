package com.acme.orders.order.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface OutboxJpaRepository extends JpaRepository<OutboxMessage, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select o from OutboxMessage o where o.status in ('PENDING','FAILED') order by o.createdAt")
  List<OutboxMessage> findPendingLocked();
}
