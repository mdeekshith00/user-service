CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);
