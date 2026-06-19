CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    usertype VARCHAR(10) NOT NULL DEFAULT 'USER',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_usertype CHECK (usertype IN ('USER', 'ADMIN'))
);

CREATE TABLE sleep_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    sleep_date DATE NOT NULL,
    bed_time TIME NOT NULL,
    wake_up_time TIME NOT NULL,
    sleep_duration TIME NOT NULL,
    morning_feeling VARCHAR(10) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sleep_log_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_morning_feeling CHECK (morning_feeling IN ('BAD', 'OK', 'GOOD')),
    CONSTRAINT uq_user_sleep_date UNIQUE (user_id, sleep_date)
);

CREATE INDEX idx_sleep_log_user_date
    ON sleep_log(user_id, sleep_date DESC);