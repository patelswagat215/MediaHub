CREATE DATABASE media_hub;
USE media_hub;
CREATE TABLE registered_user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(100),
    email VARCHAR(100),
    phone_number VARCHAR(20),
    password VARCHAR(255)
);
