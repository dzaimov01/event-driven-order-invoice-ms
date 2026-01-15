# Idempotency

## Why
Event-driven systems must handle duplicate deliveries. The Invoice Service consumes events idempotently.

## Implementation
- `processed_events` table stores processed event IDs.
- The consumer checks for an existing event ID before applying changes.
- A cleanup job removes expired rows (TTL).

## TTL Cleanup
- Default TTL is 30 days.
- Cleanup job runs nightly and can be tuned via `app.idempotency.cleanup-cron`.

## Failure Modes
- Duplicate event delivered: ignored.
- Consumer crash after processing but before ack: event retried and deduplicated.
