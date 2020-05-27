CREATE TABLE IF NOT EXISTS stock
(
    item_id    BIGINT      NOT NULL PRIMARY KEY,
    quantity   INTEGER,
    created_at TIMESTAMP   NOT NULL default now(),
    created_by VARCHAR(64) NOT NULL default 'system',
    updated_at TIMESTAMP   NOT NULL default now() ON UPDATE now(),
    updated_by VARCHAR(64) NOT NULL default 'system'
);