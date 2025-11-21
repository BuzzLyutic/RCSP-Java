CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

INSERT INTO tasks (title, description, status, created_at, updated_at)
VALUES ('Initial Task', 'This is an initial task', 'NEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
