CREATE TABLE IF NOT EXISTS stock_read (
    sku             VARCHAR(120) PRIMARY KEY,
    available_qty   BIGINT      NOT NULL,
    reserved_qty    BIGINT      NOT NULL,
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_stock_read_updated ON stock_read(updated_at DESC);