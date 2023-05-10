CREATE DATABASE newsline;

CREATE TABLE IF NOT EXISTS news(
    id serial NOT NULL,
    title varchar NOT NULL,
    date date NOT NULL,
    text varchar NOT NULL,
    image BYTEA
);

drop table news;

-- INSERT INTO news(title, date, text) VALUES ('Big boss die', '2022-01-01', 'interesting story')

