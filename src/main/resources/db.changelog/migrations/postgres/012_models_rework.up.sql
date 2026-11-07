CREATE TYPE operation_kind AS ENUM ('TECHNOLOGICAL', 'PURCHASE');

ALTER TABLE product_operation ADD COLUMN kind operation_kind NOT NULL DEFAULT 'TECHNOLOGICAL';

ALTER TABLE product_operation ALTER COLUMN qualification_id DROP NOT NULL;

ALTER TABLE product_operation ALTER COLUMN workcenter_id DROP NOT NULL;

ALTER TABLE product_operation ALTER COLUMN profession_id DROP NOT NULL;

ALTER TABLE product_operation ALTER COLUMN operation_type_id DROP NOT NULL;

CREATE TABLE operation_input (
    id BIGSERIAL PRIMARY KEY,
    output_operation_id BIGINT NOT NULL REFERENCES product_operation(id) ON DELETE CASCADE,
    input_operation_id BIGINT NOT NULL REFERENCES product_operation(id) ON DELETE CASCADE,
    quantity NUMERIC NOT NULL,
    base_quantity NUMERIC NOT NULL,
    measure_unit_id INTEGER NOT NULL REFERENCES measure_unit(id) ON DELETE CASCADE
);