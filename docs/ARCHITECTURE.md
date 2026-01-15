# Architecture

This repository demonstrates a production-grade, event-driven, DDD-aligned microservice architecture with reliable messaging patterns.

## High-Level Flow

```mermaid
sequenceDiagram
  participant Client
  participant OrderAPI as Order Service API
  participant OrderDB as Order DB
  participant Outbox as Outbox Table
  participant Publisher as Outbox Publisher
  participant Broker as Message Broker
  participant InvoiceConsumer as Invoice Service Consumer
  participant InvoiceDB as Invoice DB

  Client->>OrderAPI: POST /orders
  OrderAPI->>OrderDB: Store order (tx)
  OrderAPI->>Outbox: Store event (tx)
  OrderAPI-->>Client: 201 Created
  Publisher->>Outbox: Poll pending events
  Publisher->>Broker: Publish event
  InvoiceConsumer->>Broker: Consume event
  InvoiceConsumer->>InvoiceDB: Create invoice (idempotent)
```

## Key Components

- Order Service: manages order lifecycle, owns Outbox Pattern.
- Invoice Service: creates invoices when `OrderConfirmed` is received, with idempotency.
- Contracts: versioned event schemas in `contracts/`.

## Messaging Modes

- Kafka (default)
- RabbitMQ (optional toggle via `app.messaging.mode=rabbitmq`)
- Redis Streams (documented alternative)
