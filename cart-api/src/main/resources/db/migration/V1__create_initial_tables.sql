CREATE TABLE IF NOT EXISTS cart
(
    cart_id    VARCHAR(36) PRIMARY KEY,
    items      TEXT,
    created_at TIMESTAMP   NOT NULL default now(),
    created_by VARCHAR(64) NOT NULL default 'system',
    updated_at TIMESTAMP   NOT NULL default now() ON UPDATE now(),
    updated_by VARCHAR(64) NOT NULL default 'system'
);
