CREATE TABLE friendship (
    id INT AUTO_INCREMENT PRIMARY KEY,
    requester_id INT,
    addressee_id INT,
    FOREIGN KEY (requester_id ) REFERENCES registered_user(user_id),
    FOREIGN KEY (addressee_id) REFERENCES registered_user(user_id)
);
