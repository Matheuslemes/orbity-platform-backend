CREATE TABLE IF NOT EXISTS checkout_execution (
    id                UUID PRIMARY KEY,
    customer_id       UUID           NOT NULL,
    total_amount      NUMERIC(12,2)  NOT NULL,
    status            VARCHAR(30)    NOT NULL,
    saga_step         VARCHAR(120),
    saga_compensation VARCHAR(120),
    payload           JSONB          NOT NULL DEFAULT '{}'::jsonb,
    created_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_checkout_customer_created
    ON checkout_execution(customer_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_checkout_status_created
    ON checkout_execution(status, created_at DESC);

CREATE OR REPLACE FUNCTION trg_set_timestamp_checkout_exec()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS set_timestamp_checkout_exec ON checkout_execution;
CREATE TRIGGER set_timestamp_checkout_exec
BEFORE UPDATE ON checkout_execution
FOR EACH ROW
EXECUTE FUNCTION trg_set_timestamp_checkout_exec();
