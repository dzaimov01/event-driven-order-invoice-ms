# Operations

## Health and Metrics
- Spring Boot Actuator exposes `/actuator/health` and `/actuator/prometheus`.

## Dead Letter Handling
- Kafka: configure a DLQ consumer group and store failed payloads.
- RabbitMQ: route to a DLX exchange with dead-letter queues.

## Replay Strategy
- Use outbox table for replaying Order Service events.
- For Kafka, reprocess by offset reset in a dedicated replay consumer.

## Correlation IDs
- `X-Correlation-Id` is accepted on incoming requests.
- IDs are returned to clients and included in logs when present.

## Structured Logging
- JSON logs via Logstash encoder with MDC support.

## Monitoring
- Outbox backlog size and publish lag
- Consumer lag per partition
- Idempotency table growth and cleanup rate

## Redis Streams Alternative
- Use consumer groups and pending entries list (PEL) for at-least-once delivery.
- Keep the same outbox publisher contract with a Redis Streams adapter.
## Tracing
- OpenTelemetry API is included; configure exporter in production.
