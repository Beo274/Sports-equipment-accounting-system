CREATE TABLE IF NOT EXISTS attribute (
    id SERIAL PRIMARY KEY,
    class_id INTEGER NOT NULL REFERENCES class(id) ON DELETE CASCADE,
    name VARCHAR(256) NOT NULL,
    short_name VARCHAR(128) NOT NULL,
    string_value TEXT,
    int_value INTEGER,
    image_value TEXT,
    position INTEGER,
    measure_id INTEGER REFERENCES measure_unit(id) ON DELETE SET NULL,

    UNIQUE (class_id, name)
);

CREATE INDEX attribute_class_id_idx ON attribute(class_id);