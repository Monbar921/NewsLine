CREATE DATABASE newsline;
\c newsline;
CREATE TABLE IF NOT EXISTS news(
    id serial NOT NULL,
    title varchar NOT NULL,
    date date NOT NULL,
    text varchar NOT NULL,
    image BYTEA
);
