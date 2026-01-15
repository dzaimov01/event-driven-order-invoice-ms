# DDD Model

## Order Service

### Aggregate: Order
- Entities: OrderLine
- Value Objects: Money, CustomerId
- States: CREATED, CONFIRMED, CANCELLED

### Invariants
- Order must have at least one line.
- Confirming a cancelled order is invalid.
- Cancelling a confirmed order is invalid.

### Domain Events
- OrderCreated
- OrderConfirmed
- OrderCancelled

## Invoice Service

### Aggregate: Invoice
- Entities: InvoiceLine
- States: ISSUED, VOIDED

### Invariants
- Invoice must have at least one line.

### Domain Events
- InvoiceIssued
- InvoiceVoided
