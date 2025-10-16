CREATE TABLE orders (
    id              UUID PRIMARY KEY,
    customer_id     UUID             NOT NULL,
    total_amount    NUMERIC(12,2)    NOT NULL,
    currency        VARCHAR(3)       NOT NULL DEFAULT 'BRL',
    status          VARCHAR(30)      NOT NULL,
    created_at      TIMESTAMP        NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP        NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_customer_created ON orders(customer_id, created_at DESC);
CREATE INDEX idx_orders_status_created   ON orders(status, created_at DESC);

CREATE OR REPLACE FUNCTION trg_set_timestamp_orders()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at := now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_timestamp_orders
BEFORE UPDATE ON orders
FOR EACH ROW
EXECUTE FUNCTION trg_set_timestamp_orders();
