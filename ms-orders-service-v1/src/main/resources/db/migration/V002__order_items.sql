CREATE TABLE order_items (
    id           UUID PRIMARY KEY,
    order_id     UUID        NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id   UUID        NOT NULL,
    sku          VARCHAR(80),
    name         VARCHAR(255),
    unit_price   NUMERIC(12,2) NOT NULL,
    quantity     INTEGER     NOT NULL CHECK (quantity > 0),
    line_total   NUMERIC(12,2) NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_order_items_order ON order_items(order_id);

CREATE OR REPLACE FUNCTION trg_set_timestamp_order_items()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plsql;

CREATE TRIGGER set_timestamp_order_items
BEFORE UPDATE ON order_items
FOR EACH ROW
EXECUTE FUNCTION trg_set_timestamp_order_items();
