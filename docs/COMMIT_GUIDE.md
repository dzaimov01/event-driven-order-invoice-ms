# Suggested Commit History

Use the following sequence if you want a clean, professional history (12+ commits):

1. chore: initialize parent pom, module layout
2. feat(order): add DDD domain model and API DTOs
3. feat(order): add JPA persistence and Flyway migrations
4. feat(order): implement outbox persistence
5. feat(order): add Kafka publisher and scheduling
6. feat(invoice): add DDD model and repository adapter
7. feat(invoice): add idempotency store and cleanup job
8. feat(invoice): add Kafka consumer and REST API
9. feat(contracts): add versioned event schemas
10. test: add domain unit tests and contract tests
11. test: add integration-tests module with Testcontainers
12. docs: add architecture and ops documentation
13. ci: add GitHub Actions workflows
14. chore: add docker-compose and service Dockerfiles
