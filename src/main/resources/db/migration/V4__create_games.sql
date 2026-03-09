CREATE TABLE games (
    id                      UUID PRIMARY KEY,
    player1_id              UUID        NOT NULL REFERENCES users (id),
    player2_id              UUID                 REFERENCES users (id),
    status                  VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    current_turn_player_id  UUID,
    player1_hand            TEXT,
    player2_hand            TEXT,
    winner_id               UUID                 REFERENCES users (id),
    turn_deadline           TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_games_player1_id ON games (player1_id);
CREATE INDEX idx_games_player2_id ON games (player2_id);
CREATE INDEX idx_games_status ON games (status);
