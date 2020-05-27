CREATE TABLE IF NOT EXISTS item
(
    id         BIGINT        NOT NULL PRIMARY KEY,
    name       VARCHAR(64)   NOT NULL,
    media      VARCHAR(32)   NOT NULL,
    author     VARCHAR(64)   NOT NULL,
    unit_price DECIMAL(8, 2) NOT NULL,
    `release`  DATE          NOT NULL,
    image      VARCHAR(512)  NOT NULL,
    created_at TIMESTAMP     NOT NULL default now(),
    created_by VARCHAR(64)   NOT NULL default 'system',
    updated_at TIMESTAMP     NOT NULL default now() ON UPDATE now(),
    updated_by VARCHAR(64)   NOT NULL default 'system'
);