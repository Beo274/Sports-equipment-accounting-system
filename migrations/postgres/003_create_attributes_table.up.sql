CREATE TABLE IF NOT EXISTS attribute (
    id SERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL UNIQUE,
    short_name VARCHAR(128) NOT NULL UNIQUE,
);

CREATE TABLE IF NOT EXISTS attribute_value (
    id SERIAL PRIMARY KEY,
    attribute_id INTEGER NOT NULL REFERENCES attribute(id) ON DELETE CASCADE,
    name VARCHAR(256) NOT NULL,
    short_name VARCHAR(128) NOT NULL,
    int_value INTEGER,
    string_value TEXT,
    image_value TEXT,
    position INTEGER,
    measure_id INTEGER REFERENCES measure_unit(id) ON DELETE SET NULL
);