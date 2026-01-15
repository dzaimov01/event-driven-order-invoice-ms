# Event Versioning

## Strategy
- Event contracts are versioned under `contracts/v1`, `contracts/v2`.
- Producers emit `version` in the payload.
- Consumers should support at least the last two versions.

## Compatibility
- Additive changes (new optional fields) are preferred.
- Avoid removing fields or changing semantic meaning.
- Use `version` to branch parsing logic if needed.

## Example Evolution
- `v1` includes order lines and totals.
- `v2` adds `source` for auditing without breaking `v1` consumers.
