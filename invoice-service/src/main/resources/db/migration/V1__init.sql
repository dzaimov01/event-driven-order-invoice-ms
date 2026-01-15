CREATE TABLE invoices (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL UNIQUE,
  status VARCHAR(32) NOT NULL,
  issued_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE invoice_lines (
  id BIGSERIAL PRIMARY KEY,
  invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
  product_id UUID NOT NULL,
  quantity INT NOT NULL,
  unit_price NUMERIC(12,2) NOT NULL,
  currency VARCHAR(8) NOT NULL
);

CREATE TABLE processed_events (
  event_id UUID PRIMARY KEY,
  received_at TIMESTAMPTZ NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_processed_events_expires ON processed_events(expires_at);
