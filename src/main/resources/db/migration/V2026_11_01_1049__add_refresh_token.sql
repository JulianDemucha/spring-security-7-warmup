CREATE TABLE refresh_token
(
    id         UUID                     NOT NULL,
    token_hash VARCHAR(255)             NOT NULL,
    user_id    BIGINT                   NOT NULL,
    family_id  UUID                     NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    rotated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id),
    CONSTRAINT uq_refresh_token_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES app_user (id)
);

CREATE INDEX idx_refresh_token_family_id ON refresh_token (family_id);