--liquibase formatted sql

--changeset kristaps:1

CREATE TABLE airport
(
    country varchar(255)             NOT NULL,
    city    varchar(255)             NOT NULL,
    airport varchar(255) PRIMARY KEY NOT NULL
);