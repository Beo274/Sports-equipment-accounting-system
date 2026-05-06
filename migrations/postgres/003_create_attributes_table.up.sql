CREATE TABLE IF NOT EXISTS enumeration (
    id SERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL UNIQUE,
    short_name VARCHAR(128) NOT NULL UNIQUE,
);

CREATE TABLE IF NOT EXISTS enumeration_value (
    id SERIAL PRIMARY KEY,
    enumeration_id INTEGER NOT NULL REFERENCES enumeration(id) ON DELETE CASCADE,
    int_value INTEGER,
    string_value TEXT,
    image_value TEXT,
    position INTEGER,
    measure_id INTEGER REFERENCES measure_unit(id) ON DELETE SET NULL
);