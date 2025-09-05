CREATE TABLE IF NOT EXISTS song_metadata (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    artist VARCHAR(255),
    album VARCHAR(255),
    duration VARCHAR(50),
    year INT
);