-- liquibase formatted sql
-- changeset your_name:alter-product-change-id
ALTER TABLE product_change
    ALTER COLUMN product_id TYPE INTEGER;