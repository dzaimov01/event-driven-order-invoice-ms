package com.acme.orders.invoice.infrastructure.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {
  @Id
  private UUID id;

  @Column(name = "order_id", nullable = false, unique = true)
  private UUID orderId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InvoiceStatusEntity status;

  @Column(name = "issued_at", nullable = false)
  private Instant issuedAt;

  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InvoiceLineEntity> lines = new ArrayList<>();

  protected InvoiceEntity() {
  }

  public InvoiceEntity(UUID id, UUID orderId, InvoiceStatusEntity status, Instant issuedAt) {
    this.id = id;
    this.orderId = orderId;
    this.status = status;
    this.issuedAt = issuedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public InvoiceStatusEntity getStatus() {
    return status;
  }

  public Instant getIssuedAt() {
    return issuedAt;
  }

  public List<InvoiceLineEntity> getLines() {
    return lines;
  }

  public void setLines(List<InvoiceLineEntity> lines) {
    this.lines = lines;
  }

  public void setStatus(InvoiceStatusEntity status) {
    this.status = status;
  }
}
