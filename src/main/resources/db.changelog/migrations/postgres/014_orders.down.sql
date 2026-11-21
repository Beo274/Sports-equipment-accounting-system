ALTER TABLE product_parameter DROP COLUMN is_configuration;

ALTER TABLE product DROP CONSTRAINT product_type_base_product;

ALTER TABLE product DROP COLUMN product_type;

DROP TYPE product_types;

DROP TABLE IF EXISTS order_item;

DROP TABLE IF EXISTS orders;

DROP TYPE IF EXISTS order_status;

DROP TABLE IF EXISTS organization;