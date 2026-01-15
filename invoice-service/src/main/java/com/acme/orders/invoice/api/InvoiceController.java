package com.acme.orders.invoice.api;

import com.acme.orders.invoice.application.InvoiceQueryService;
import com.acme.orders.invoice.domain.Invoice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
  private final InvoiceQueryService queryService;

  public InvoiceController(InvoiceQueryService queryService) {
    this.queryService = queryService;
  }

  @GetMapping("/{id}")
  public InvoiceResponse getById(@PathVariable("id") UUID id) {
    return InvoiceApiMapper.toResponse(queryService.getById(id));
  }

  @GetMapping
  public InvoiceResponse getByOrderId(@RequestParam("orderId") UUID orderId) {
    return InvoiceApiMapper.toResponse(queryService.getByOrderId(orderId));
  }
}
