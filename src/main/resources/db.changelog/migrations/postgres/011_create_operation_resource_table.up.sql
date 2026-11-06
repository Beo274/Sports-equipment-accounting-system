CREATE TABLE operation_resource
(
    id                   BIGSERIAL PRIMARY KEY,
    product_operation_id BIGINT      NOT NULL REFERENCES product_operation (id) ON DELETE CASCADE,
    resource_id          INTEGER     NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    resource_type        VARCHAR(64) NOT NULL,
    quantity             BIGINT      NOT NULL DEFAULT 1,
    base_quantity        BIGINT      NOT NULL DEFAULT 1,
    measure_unit_id      INTEGER     NOT NULL REFERENCES measure_unit (id),

    CONSTRAINT chk_operation_resource_quantity CHECK (quantity > 0),
    CONSTRAINT chk_operation_resource_base_quantity CHECK (base_quantity > 0)
);