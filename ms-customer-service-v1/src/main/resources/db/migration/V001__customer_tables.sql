CREATE TABLE customers (
    id              UUID PRIMARY KEY,
    sub             VARCHAR(190) UNIQUE,
    email           VARCHAR(190) UNIQUE NOT NULL,
    first_name      VARCHAR(120),
    last_name       VARCHAR(120),
    phone           VARCHAR(40),
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE addresses (
    id              UUID PRIMARY KEY,
    customer_id     UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    label           VARCHAR(80), --"home", "office", etc
    street          VARCHAR(255),
    number          VARCHAR(40),
    complement      VARCHAR(120),
    district        VARCHAR(120),
    city            VARCHAR(120),
    state           VARCHAR(80),
    country         VARCHAR(80),
    zip             VARCHAR(20),
    main            BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE consents (
    customer_id        UUID PRIMARY KEY REFERENCES customers(id) ON DELETE CASCADE,
    marketing_option    BOOLEAN NOT NULL DEFAULT false,
    terms_accepted      BOOLEAN NOT NULL DEFAULT false,
    data_processing     BOOLEAN NOT NULL DEFAULT false,
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_sub   ON customers(sub);