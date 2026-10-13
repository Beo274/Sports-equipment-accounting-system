--liquibase formatted sql

--changeset roman: 008

CREATE TABLE product_change (
    id SERIAL PRIMARY KEY,
    change_date DATE,
    product_id INTEGER REFERENCES product(id),
    description VARCHAR(256)
)