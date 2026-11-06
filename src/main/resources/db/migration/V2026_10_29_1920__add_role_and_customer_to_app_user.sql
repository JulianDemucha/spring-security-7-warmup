ALTER TABLE app_user
    ADD COLUMN role        VARCHAR(20) NOT NULL,
    ADD COLUMN customer_id BIGINT,
    ADD CONSTRAINT fk_app_user_customer FOREIGN KEY (customer_id) REFERENCES customer (id),
    ADD CONSTRAINT uq_app_user_customer_id UNIQUE (customer_id),
    ADD CONSTRAINT ck_app_user_role_customer CHECK (
        (role = 'CLIENT' AND customer_id IS NOT NULL) OR
        (role = 'ADMIN' AND customer_id IS NULL)
    );
