CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS public.outbox_event (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  aggregate_type VARCHAR(100)  NOT NULL,
  aggregate_id   VARCHAR(100)  NOT NULL,
  type           VARCHAR(100)  NOT NULL,
  payload        JSONB         NOT NULL,
  headers        JSONB,
  occurred_on    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  published      BOOLEAN       NOT NULL DEFAULT FALSE,
  published_on   TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_outbox_event_published_occurred
  ON public.outbox_event (published, occurred_on);

CREATE INDEX IF NOT EXISTS idx_outbox_event_aggregate
  ON public.outbox_event (aggregate_type, aggregate_id);
