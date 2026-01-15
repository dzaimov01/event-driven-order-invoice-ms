# Limitations and Advanced Modules

This repo implements core reliability patterns while leaving advanced production hardening as extension points.

## Advanced Modules (Extension Points)
- Saga orchestration (compensations): interface only, no implementation.
- Schema registry integration: docs only.
- Multi-region / partition strategy: docs only.
- Advanced retry backoff tuning: docs only.
- Security hardening (mTLS, ACLs, authN/authZ for event bus): docs + placeholders.
- Performance tuning and load testing pack: docs only.

## Exactly-Once Semantics
True exactly-once is hard because it requires distributed transactions across services and brokers. This repo uses:
- Outbox pattern for at-least-once publishing.
- Idempotent consumers to handle duplicates.

This combination achieves effective exactly-once processing for the business outcome.
