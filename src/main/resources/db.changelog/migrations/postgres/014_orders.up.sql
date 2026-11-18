CREATE TABLE organization (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TYPE order_status AS ENUM (
    'CREATED',
    'ACCEPTED',
    'IN_PROGRESS',
    'DONE',
    'CANCELLED'
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    status order_status NOT NULL DEFAULT 'CREATED',
    organization_id BIGINT NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    position_number BIGINT NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 1,
    unit_price NUMERIC(15, 2) NOT NULL DEFAULT 0
);

CREATE TYPE product_types AS ENUM (
    'SIMPLE',
    'CONFIGURABLE',
    'VARIANT'
);

ALTER TABLE product ADD COLUMN product_type product_types NOT NULL DEFAULT 'SIMPLE';

ALTER TABLE product ADD CONSTRAINT product_type_base_product 
    CHECK (
        base_product_id = NULL AND product_type = 'SIMPLE' 
        OR base_product_id != NULL AND (product_type = 'CONFIGURABLE' OR product_type = 'VARIANT')
    );

ALTER TABLE product_parameter ADD COLUMN is_configuration BOOLEAN NOT NULL DEFAULT FALSE;

