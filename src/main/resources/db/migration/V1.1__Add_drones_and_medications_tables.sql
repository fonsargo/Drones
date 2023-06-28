CREATE TABLE drone
(
    id                  BIGSERIAL PRIMARY KEY,
    serial_number       VARCHAR(100) NOT NULL UNIQUE,
    model               TEXT NOT NULL,
    weight_limit        INT NOT NULL,
    battery_capacity    INT NOT NULL,
    state               TEXT NOT NULL
);

CREATE INDEX ON drone (state);

CREATE TABLE medication
(
    drone       BIGINT REFERENCES drone(id) ON DELETE CASCADE,
    drone_key   INT NOT NULL,
    name        TEXT NOT NULL,
    weight      INT NOT NULL,
    code        TEXT NOT NULL,
    image       TEXT NOT NULL
);
