CREATE TABLE turns (
    id                 UUID PRIMARY KEY,
    game_id            UUID        NOT NULL REFERENCES games (id) ON DELETE CASCADE,
    turn_number        INT         NOT NULL,
    active_player_id   UUID        NOT NULL REFERENCES users (id),
    player1_card_id    UUID        NOT NULL REFERENCES cards (id),
    player2_card_id    UUID        NOT NULL REFERENCES cards (id),
    chosen_stat        VARCHAR(20) NOT NULL,
    player1_stat_value INT         NOT NULL,
    player2_stat_value INT         NOT NULL,
    winner_player_id   UUID                 REFERENCES users (id),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_turns_game_id ON turns (game_id);
