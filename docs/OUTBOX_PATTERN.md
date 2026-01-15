# Outbox Pattern

## Why
The outbox pattern prevents lost events by persisting integration events in the same transaction as domain state changes. A separate publisher reliably dispatches events to the broker.

## Implementation
- Order Service writes to `outbox` table in the same transaction as the order change.
- `OutboxPublisher` polls PENDING rows and publishes to Kafka or RabbitMQ.
- After success, status is set to PUBLISHED. Failures increment `retry_count`.

## Guarantees
- At-least-once delivery
- No dual-write anomalies

## Retry Strategy
- Periodic polling with fixed delay.
- On failure, message remains with `FAILED` status and incremented `retry_count`.
- Operator can replay FAILED events after remediation.

## Failure Modes
- Broker down: events remain in outbox and are retried.
- Publisher crash mid-send: events are reprocessed (idempotent consumer required).
