CREATE TABLE IF NOT EXISTS missions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    launch_year INTEGER,
    status VARCHAR(50) DEFAULT 'PLANNED',
    crew_size INTEGER DEFAULT 0
);