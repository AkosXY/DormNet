CREATE DATABASE IF NOT EXISTS accommodation_service;

use accommodation_service;

CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(255),
    phone VARCHAR(255),
    capacity INT,
    num_of_residents INT
);

CREATE TABLE IF NOT EXISTS residents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    username VARCHAR(255),
    room_id BIGINT,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL
);
