CREATE TABLE `users`
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    email        VARCHAR(100) NOT NULL UNIQUE,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    birthdate    DATE         NOT NULL,
    address      VARCHAR(100),
    phone_number VARCHAR(25),
    PRIMARY KEY (id)
);