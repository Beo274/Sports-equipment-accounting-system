CREATE TABLE technology_process_template (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL DEFAULT ''
);

CREATE TABLE technology_process_template_operation (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL REFERENCES technology_process_template(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    kind operation_kind NOT NULL DEFAULT 'TECHNOLOGICAL',
    operation_number BIGINT NOT NULL DEFAULT 1,
    setup_time BIGINT NOT NULL DEFAULT 0,
    piece_time BIGINT NOT NULL DEFAULT 0,
    operation_type_id BIGINT REFERENCES operation_type(id),
    workcenter_id BIGINT REFERENCES work_center(id),
    profession_id BIGINT REFERENCES profession(id),
    qualification_id BIGINT REFERENCES qualification(id)
);

CREATE TABLE technology_process_template_input (
    id BIGSERIAL PRIMARY KEY,
    output_template_operation_id BIGINT NOT NULL REFERENCES technology_process_template_operation(id) ON DELETE CASCADE,
    input_template_operation_id BIGINT REFERENCES technology_process_template_operation(id) ON DELETE CASCADE,
    external_input_operation_id BIGINT REFERENCES product_operation(id) ON DELETE SET NULL,
    quantity NUMERIC(19, 6) NOT NULL,
    base_quantity NUMERIC(19, 6) NOT NULL DEFAULT 1,
    measure_unit_id INTEGER REFERENCES measure_unit(id) ON DELETE SET NULL
);

CREATE INDEX idx_tpt_operation_template_id
    ON technology_process_template_operation(template_id);

CREATE INDEX idx_tpt_input_output_op_id
    ON technology_process_template_input(output_template_operation_id);