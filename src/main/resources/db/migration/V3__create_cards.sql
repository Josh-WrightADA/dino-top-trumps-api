CREATE TABLE cards (
    id           UUID PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    meaning      VARCHAR(255),
    diet         VARCHAR(50),
    era          VARCHAR(100),
    image_url    VARCHAR(500),
    height       INT NOT NULL,
    weight       INT NOT NULL,
    intelligence INT NOT NULL,
    speed        INT NOT NULL,
    strength     INT NOT NULL
);
