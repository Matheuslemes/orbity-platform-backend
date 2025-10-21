CREATE TABLE IF NOT EXISTS outbox (
    id            UUID PRIMARY KEY,
    aggregate_id  UUID,
    event_type    VARCHAR(120) NOT NULL,
    payload       TEXT         NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    published     BOOLEAN      NOT NULL DEFAULT false,
    published_at  TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_outbox_unpublished ON outbox(published, created_at);
