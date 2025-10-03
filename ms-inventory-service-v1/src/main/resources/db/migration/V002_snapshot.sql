-- Snapshots opcionais para reidratar o agregado rapidamente

CREATE TABLE IF NOT EXISTS snapshot_store (
    aggregate_id    UUID PRIMARY KEY,
    version         BIGINT      NOT NULL,
    snapshot_store  JSON        NOT NULL,
    taken_at        TIMESTAMP   NOT NULL DEFAULT NOW()
);