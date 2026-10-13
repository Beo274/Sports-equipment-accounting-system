--liquibase formatted sql

--changeset roman: 007

ALTER TABLE product
    ADD COLUMN base_product_id INTEGER;

ALTER TABLE product
ADD CONSTRAINT fk_base_product_id
FOREIGN KEY (base_product_id)
REFERENCES product(id)
ON DELETE CASCADE;