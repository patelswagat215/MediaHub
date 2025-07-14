CREATE TABLE likes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT,
    user_id INT,
    FOREIGN KEY (post_id) REFERENCES user_posts(id),
    FOREIGN KEY (user_id) REFERENCES registered_user(user_id)
); 