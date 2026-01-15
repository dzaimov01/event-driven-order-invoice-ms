package com.acme.orders.invoice.infrastructure.persistence;

import com.acme.orders.invoice.application.InvoiceRepository;
import com.acme.orders.invoice.domain.Invoice;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class InvoiceRepositoryAdapter implements InvoiceRepository {
  private final InvoiceJpaRepository repository;

  public InvoiceRepositoryAdapter(InvoiceJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Invoice save(Invoice invoice) {
    InvoiceEntity saved = repository.save(InvoiceMapper.toEntity(invoice));
    return InvoiceMapper.toDomain(saved);
  }

  @Override
  public Optional<Invoice> findById(UUID id) {
    return repository.findById(id).map(InvoiceMapper::toDomain);
  }

  @Override
  public Optional<Invoice> findByOrderId(UUID orderId) {
    return repository.findByOrderId(orderId).map(InvoiceMapper::toDomain);
  }
}
