CREATE TABLE outbox (
    id            UUID PRIMARY KEY,
    aggregate_id  UUID         NOT NULL,
    event_type    VARCHAR(120) NOT NULL,
    payload       JSONB        NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    published     BOOLEAN      NOT NULL DEFAULT false,
    published_at  TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished ON outbox(published, created_at);
