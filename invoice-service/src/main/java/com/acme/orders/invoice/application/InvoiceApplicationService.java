package com.acme.orders.invoice.application;

import com.acme.orders.invoice.domain.Invoice;
import com.acme.orders.invoice.domain.InvoiceIssued;
import com.acme.orders.invoice.domain.InvoiceLine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class InvoiceApplicationService {
  private final InvoiceRepository invoiceRepository;

  public InvoiceApplicationService(InvoiceRepository invoiceRepository) {
    this.invoiceRepository = invoiceRepository;
  }

  @Transactional
  public Invoice issueInvoice(UUID orderId, List<InvoiceLine> lines) {
    Invoice invoice = Invoice.issue(orderId, lines);
    Invoice saved = invoiceRepository.save(invoice);
    InvoiceIssued.now(saved.id(), saved.orderId());
    return saved;
  }
}
