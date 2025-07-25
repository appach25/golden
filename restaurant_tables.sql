-- Table definition for restaurant tables in PostgreSQL
CREATE TABLE IF NOT EXISTS tables (
    numero SERIAL PRIMARY KEY,
    nb_places INTEGER NOT NULL CHECK (nb_places > 0)
);
