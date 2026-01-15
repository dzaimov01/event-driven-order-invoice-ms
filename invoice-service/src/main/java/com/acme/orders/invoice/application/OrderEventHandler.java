package com.acme.orders.invoice.application;

import com.acme.orders.invoice.domain.Invoice;
import com.acme.orders.invoice.domain.InvoiceLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class OrderEventHandler {
  private static final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);
  private static final int DEFAULT_TTL_DAYS = 30;

  private final ProcessedEventRepository processedEventRepository;
  private final InvoiceRepository invoiceRepository;

  public OrderEventHandler(ProcessedEventRepository processedEventRepository, InvoiceRepository invoiceRepository) {
    this.processedEventRepository = processedEventRepository;
    this.invoiceRepository = invoiceRepository;
  }

  @Transactional
  public void handle(OrderEventPayload payload) {
    if (processedEventRepository.exists(payload.eventId())) {
      log.info("event_deduplicated eventId={} orderId={} type={}", payload.eventId(), payload.orderId(), payload.type());
      return;
    }

    if ("OrderConfirmed".equals(payload.type())) {
      if (invoiceRepository.findByOrderId(payload.orderId()).isPresent()) {
        processedEventRepository.record(payload.eventId(), expiresAt());
        return;
      }
      Invoice invoice = Invoice.issue(payload.orderId(), toLines(payload));
      invoiceRepository.save(invoice);
    } else if ("OrderCancelled".equals(payload.type())) {
      invoiceRepository.findByOrderId(payload.orderId()).ifPresent(existing -> {
        existing.voidInvoice();
        invoiceRepository.save(existing);
      });
    }

    processedEventRepository.record(payload.eventId(), expiresAt());
  }

  private List<InvoiceLine> toLines(OrderEventPayload payload) {
    return payload.lines().stream()
        .map(line -> new InvoiceLine(line.productId(), line.quantity(), line.unitPrice(), line.currency()))
        .toList();
  }

  private Instant expiresAt() {
    return Instant.now().plus(DEFAULT_TTL_DAYS, ChronoUnit.DAYS);
  }
}
