package com.acme.orders.invoice.application;

import com.acme.orders.invoice.domain.Invoice;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {
  Invoice save(Invoice invoice);
  Optional<Invoice> findById(UUID id);
  Optional<Invoice> findByOrderId(UUID orderId);
}
