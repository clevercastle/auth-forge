-- Drop tables if they exist (for test cleanup)
DROP TABLE IF EXISTS user_login_items CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create users table
CREATE TABLE users
(
    user_id                        VARCHAR(128) PRIMARY KEY,
    user_state                     VARCHAR(32)  NOT NULL,
    hashed_password                VARCHAR(512) NOT NULL,
    reset_password_code            VARCHAR(128),
    reset_password_code_expired_at TIMESTAMPTZ,
    created_at                     TIMESTAMPTZ  NOT NULL,
    updated_at                     TIMESTAMPTZ  NOT NULL
);

-- Create user_login_items table with composite primary key
CREATE TABLE user_login_items
(
    login_identifier_type VARCHAR(64)  NOT NULL,
    login_identifier      VARCHAR(256) NOT NULL,
    type                  VARCHAR(32)  NOT NULL,
    sso_sub               VARCHAR(256),
    user_sub              VARCHAR(128) NOT NULL UNIQUE,
    user_id               VARCHAR(128) NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    state                 VARCHAR(32)  NOT NULL,
    created_at            TIMESTAMPTZ  NOT NULL,
    updated_at            TIMESTAMPTZ  NOT NULL,
    PRIMARY KEY (login_identifier_type, login_identifier)
);

-- Create indexes for common queries
CREATE INDEX idx_user_login_items_user_id ON user_login_items (user_id);
CREATE INDEX idx_user_login_items_user_sub ON user_login_items (user_sub);
CREATE INDEX idx_user_login_items_type ON user_login_items (type);
