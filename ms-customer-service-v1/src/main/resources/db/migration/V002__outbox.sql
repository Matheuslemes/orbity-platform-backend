CREATE TABLE outbox (
    id              BIGSERIAL PRIMARY KEY,
    aggregate_id    UUID NOT NULL,
    event_type      VARCHAR(120) NOT NULL,
    payload         JSONB NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now();
    published       BOOLEAN NOT NULL DEFAULT false,
    published_at    TIMESTAMP
);

CREATE INDEX idx_outbox_unpublished ON outbox(published, created_at);