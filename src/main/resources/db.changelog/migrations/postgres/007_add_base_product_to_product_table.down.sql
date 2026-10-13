--liquibase formatted sql

--changeset roman: 007

ALTER TABLE product
    DROP CONSTRAINT fk_base_product_id;

ALTER TABLE product
    DROP COLUMN base_product_id;