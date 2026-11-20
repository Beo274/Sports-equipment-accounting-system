CREATE TABLE product_parameter_allowed_enum_values (
    product_parameter_id BIGINT NOT NULL REFERENCES product_parameter (id) ON DELETE CASCADE,
    enumeration_value_id BIGINT NOT NULL REFERENCES enumeration_value (id),

    CONSTRAINT pk_product_parameter_allowed_values
        PRIMARY KEY (product_parameter_id, enumeration_value_id)
);