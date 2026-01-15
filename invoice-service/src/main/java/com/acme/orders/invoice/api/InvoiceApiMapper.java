package com.acme.orders.invoice.api;

import com.acme.orders.invoice.domain.Invoice;

public final class InvoiceApiMapper {
  private InvoiceApiMapper() {
  }

  public static InvoiceResponse toResponse(Invoice invoice) {
    return new InvoiceResponse(
        invoice.id(),
        invoice.orderId(),
        invoice.status().name(),
        invoice.issuedAt(),
        invoice.lines().stream()
            .map(line -> new InvoiceLineResponse(line.productId(), line.quantity(), line.unitPrice(), line.currency()))
            .toList()
    );
  }
}
