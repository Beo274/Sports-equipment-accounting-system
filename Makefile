DB_USER ?= default
DB_PASSWORD ?= default_pass
DB_NAME ?= sportsdb

OUTPUT_FILE_ENV = ./deploy/.env

add_test_env:
	@echo "POSTGRES_USER=$(DB_USER)" > $(OUTPUT_FILE_ENV)
	@echo "POSTGRES_PASSWORD=$(DB_PASS)" >> $(OUTPUT_FILE_ENV)
	@echo "POSTGRES_DB=$(DB_NAME)" >> $(OUTPUT_FILE_ENV)