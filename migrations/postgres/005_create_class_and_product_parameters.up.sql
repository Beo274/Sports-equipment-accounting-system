CREATE TABLE IF NOT EXISTS class_parameter (
    id SERIAL PRIMARY KEY,
    class_id INTEGER NOT NULL REFERENCES class(id) ON DELETE CASCADE,
    param_id INTEGER NOT NULL REFERENCES parameter(id) ON DELETE CASCADE,
    enumeration_value_id INTEGER REFERENCES enumeration_value(id) ON DELETE SET NULL,
    max_val INTEGER,
    min_val INTEGER,
    int_val INTEGER,

    CONSTRAINT int_val CHECK (int_val >= min_val AND int_val <= max_val)
);

CREATE TABLE IF NOT EXISTS product_parameter (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    param_id INTEGER NOT NULL REFERENCES parameter(id) ON DELETE CASCADE,
    enumeration_value_id INTEGER REFERENCES enumeration_value(id) ON DELETE SET NULL,
    max_val INTEGER,
    min_val INTEGER,
    int_val INTEGER,

    CONSTRAINT int_val CHECK (int_val >= min_val AND int_val <= max_val)
);