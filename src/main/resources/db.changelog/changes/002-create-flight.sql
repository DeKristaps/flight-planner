--liquibase formatted sql

--changeset kristaps:2

CREATE TABLE flight
(
    id             bigint PRIMARY KEY,
    from_airport   VARCHAR(10)  NOT NULL,
    to_airport     varchar(10)  NOT NULL,
    carrier        VARCHAR(255) NOT NULL,
    departure_time TIMESTAMP    NOT NULL,
    arrival_time   TIMESTAMP    NOT NULL,
    FOREIGN KEY (from_airport) REFERENCES airport (airport) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (to_airport) REFERENCES airport (airport) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE SEQUENCE id_sequence
    INCREMENT BY 1;
