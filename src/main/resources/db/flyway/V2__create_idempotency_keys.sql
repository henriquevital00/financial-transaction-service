CREATE TABLE IF NOT EXISTS idempotency_keys (
    idempotency_key VARCHAR(36) PRIMARY KEY,
    request_path VARCHAR(255) NOT NULL,
    response_status INT,
    response_body TEXT,
    locked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_idempotency_created_at ON idempotency_keys(created_at);
