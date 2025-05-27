CREATE TABLE user_posts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    caption VARCHAR(255),
    media_type VARCHAR(100),
    media_url VARCHAR(500),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES registered_user(user_id)
);
