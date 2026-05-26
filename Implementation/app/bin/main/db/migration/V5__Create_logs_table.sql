CREATE TABLE logs (
    id BIGSERIAL PRIMARY KEY,
    datetime TIMESTAMP NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(255) NOT NULL,
    initiator_username VARCHAR(50) NOT NULL
);