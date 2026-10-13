CREATE TABLE IF NOT EXISTS bom (
    id SERIAL PRIMARY KEY,
    parent_product_id INTEGER REFERENCES product(id) ON DELETE CASCADE,
    component_id INTEGER REFERENCES product(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    for_quantity INTEGER NOT NULL CHECK (for_quantity > 0),
    measure_unit_id INTEGER REFERENCES measure_unit(id) ON DELETE SET NULL,
    is_base BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS bom_parent_product_id_idx ON bom(parent_product_id);