package com.acme.orders.invoice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, UUID> {
  Optional<InvoiceEntity> findByOrderId(UUID orderId);
}
