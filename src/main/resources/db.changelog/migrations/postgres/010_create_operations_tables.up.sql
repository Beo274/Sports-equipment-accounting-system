CREATE TABLE operation_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL DEFAULT ''
);

CREATE TABLE profession (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL DEFAULT ''
);

CREATE TABLE qualification (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL DEFAULT ''
);

CREATE TABLE work_center (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL DEFAULT ''
);

CREATE TABLE product_operation (
    id BIGSERIAL PRIMARY KEY,

    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    operation_number BIGINT NOT NULL DEFAULT 1,

    name VARCHAR(255) NOT NULL,

    operation_type_id BIGINT NOT NULL REFERENCES operation_type(id),
    workcenter_id BIGINT NOT NULL REFERENCES work_center(id),
    profession_id BIGINT NOT NULL REFERENCES profession(id),
    qualification_id BIGINT NOT NULL REFERENCES qualification(id),

    setup_time BIGINT NOT NULL DEFAULT 0,
    piece_time BIGINT NOT NULL DEFAULT 0,

    description TEXT NOT NULL DEFAULT '',

    CONSTRAINT chk_product_operation_number
        CHECK (operation_number > 0),

    CONSTRAINT chk_product_operation_setup_time
        CHECK (setup_time >= 0),

    CONSTRAINT chk_product_operation_piece_time
        CHECK (piece_time >= 0)
);

CREATE INDEX idx_product_operation_product_id
    ON product_operation(product_id);

CREATE INDEX idx_product_operation_type_id
    ON product_operation(operation_type_id);

CREATE INDEX idx_product_operation_workcenter_id
    ON product_operation(workcenter_id);

CREATE INDEX idx_product_operation_profession_id
    ON product_operation(profession_id);

CREATE INDEX idx_product_operation_qualification_id
    ON product_operation(qualification_id);

CREATE INDEX idx_product_operation_product_number
    ON product_operation(product_id, operation_number);