package com.acme.orders.invoice;

import com.acme.orders.invoice.domain.Invoice;
import com.acme.orders.invoice.domain.InvoiceLine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvoiceDomainTest {

  @Test
  void issueInvoiceRequiresLines() {
    assertThrows(IllegalArgumentException.class, () ->
        new Invoice(UUID.randomUUID(), UUID.randomUUID(), List.of(), com.acme.orders.invoice.domain.InvoiceStatus.ISSUED, java.time.Instant.now()));
  }

  @Test
  void voidUpdatesStatus() {
    Invoice invoice = Invoice.issue(UUID.randomUUID(), List.of(sampleLine()));
    invoice.voidInvoice();
    assertEquals(com.acme.orders.invoice.domain.InvoiceStatus.VOIDED, invoice.status());
  }

  private InvoiceLine sampleLine() {
    return new InvoiceLine(UUID.randomUUID(), 1, new BigDecimal("12.50"), "USD");
  }
}
