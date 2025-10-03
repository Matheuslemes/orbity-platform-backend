-- Tabelas de preço com versionamento e vigência (PostgreSQL)

CREATE TABLE IF NOT EXISTS public.price (
    id                 UUID PRIMARY KEY,
    sku                VARCHAR(100) NOT NULL UNIQUE,
    currency           VARCHAR(3)   NOT NULL,
    amount_cents       BIGINT       NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.price_version (
    id                 UUID PRIMARY KEY,
    price_id           UUID NOT NULL REFERENCES public.price(id) ON DELETE CASCADE,
    amount_cents       BIGINT       NOT NULL,
    currency           VARCHAR(3)   NOT NULL,
    valid_from         TIMESTAMPTZ  NOT NULL,
    valid_to           TIMESTAMPTZ,
    is_active          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS ix_price_version_price_id ON public.price_version(price_id);
CREATE INDEX IF NOT EXISTS ix_price_version_active   ON public.price_version(is_active);
