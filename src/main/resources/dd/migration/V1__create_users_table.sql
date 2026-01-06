CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,

    -- FullName (Embedded)
    full_name_first_name VARCHAR(100) NOT NULL,
    full_name_middle_name VARCHAR(100),
    full_name_last_name VARCHAR(100) NOT NULL,

    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    is_phone_number_verified BOOLEAN DEFAULT FALSE,
    gender VARCHAR(10),
    e_mail VARCHAR(255) NOT NULL,
    address_type VARCHAR(100) NOT NULL,

    -- Address (Embedded)
    address_house_no VARCHAR(100),
    address_street_name VARCHAR(100),
    address_city VARCHAR(100),
    address_state VARCHAR(100),
    address_country VARCHAR(100),
    address_pincode VARCHAR(10),

    date_of_birth DATE,
    is_active BOOLEAN DEFAULT TRUE,
    active_status VARCHAR(50),
    login_count BIGINT DEFAULT 0,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reset_token VARCHAR(255),
    bio TEXT,
    log_in_provider VARCHAR(100),
    want_to_donate BOOLEAN DEFAULT FALSE
);
