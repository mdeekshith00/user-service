CREATE TABLE UserHistory (
    user_history_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_description VARCHAR(255),
    created_at TIMESTAMP,
    note VARCHAR(255),
    user_id INT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES Users(user_id)
);
