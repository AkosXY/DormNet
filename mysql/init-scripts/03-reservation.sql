CREATE DATABASE IF NOT EXISTS reservation_service;

use reservation_service;

CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_number VARCHAR(255),
    reservation_name VARCHAR(255),
    resource_id BIGINT,
    email VARCHAR(255),
    start_date DATETIME,
    stop_date DATETIME
);
