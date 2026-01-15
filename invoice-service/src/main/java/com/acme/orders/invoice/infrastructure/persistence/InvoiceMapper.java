package com.acme.orders.invoice.infrastructure.persistence;

import com.acme.orders.invoice.domain.Invoice;
import com.acme.orders.invoice.domain.InvoiceLine;
import com.acme.orders.invoice.domain.InvoiceStatus;

import java.util.ArrayList;
import java.util.List;

public final class InvoiceMapper {
  private InvoiceMapper() {
  }

  public static InvoiceEntity toEntity(Invoice invoice) {
    InvoiceEntity entity = new InvoiceEntity(invoice.id(), invoice.orderId(),
        InvoiceStatusEntity.valueOf(invoice.status().name()), invoice.issuedAt());
    List<InvoiceLineEntity> lineEntities = new ArrayList<>();
    for (InvoiceLine line : invoice.lines()) {
      lineEntities.add(new InvoiceLineEntity(entity, line.productId(), line.quantity(),
          line.unitPrice(), line.currency()));
    }
    entity.setLines(lineEntities);
    return entity;
  }

  public static Invoice toDomain(InvoiceEntity entity) {
    List<InvoiceLine> lines = new ArrayList<>();
    for (InvoiceLineEntity line : entity.getLines()) {
      lines.add(new InvoiceLine(line.getProductId(), line.getQuantity(), line.getUnitPrice(), line.getCurrency()));
    }
    return new Invoice(entity.getId(), entity.getOrderId(), lines,
        InvoiceStatus.valueOf(entity.getStatus().name()), entity.getIssuedAt());
  }
}
