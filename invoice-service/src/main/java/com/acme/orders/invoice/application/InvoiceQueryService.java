package com.acme.orders.invoice.application;

import com.acme.orders.invoice.domain.Invoice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InvoiceQueryService {
  private final InvoiceRepository invoiceRepository;

  public InvoiceQueryService(InvoiceRepository invoiceRepository) {
    this.invoiceRepository = invoiceRepository;
  }

  @Transactional(readOnly = true)
  public Invoice getById(UUID id) {
    return invoiceRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
  }

  @Transactional(readOnly = true)
  public Invoice getByOrderId(UUID orderId) {
    return invoiceRepository.findByOrderId(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
  }
}
