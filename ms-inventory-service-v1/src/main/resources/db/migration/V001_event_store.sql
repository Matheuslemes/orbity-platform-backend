-- tabela append-only para eventos de dominio de estoque

CREATE TABLE IF NOT EXISTS event_store (
    id              BIGSERIAL PRIMARY KEY,
    aggregate_id    UUID        NOT NULL,
    aggregate_type  VARCHAR(80) NOT NULL DEFAULT 'StockAggregate',
    version         BIGINT      NOT NULL,
    event_type      VARCHAR(120) NOT NULL,
    event_payload   JSONB       NOT NULL,
    metadata        JSONB       NULL,
    occurred_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE(aggregate_id, version)
);

CREATE INDEX IF NOT EXISTS idx_event_store_agg ON event_store(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_event_store_type ON event_store(event_type);