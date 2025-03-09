CREATE DATABASE IF NOT EXISTS api_gateway;
use api_gateway;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    name VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(255),
    permissions VARCHAR(255)
);
