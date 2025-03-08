CREATE DATABASE IF NOT EXISTS resource_service;

CREATE TABLE IF NOT EXISTS resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    status VARCHAR(255),
    available BOOLEAN,
    version BIGINT
);

