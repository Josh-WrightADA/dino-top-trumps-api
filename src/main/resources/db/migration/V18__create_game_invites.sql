CREATE TABLE game_invites (
    id          UUID PRIMARY KEY,
    game_id     UUID         NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    inviter_id  UUID         NOT NULL REFERENCES users(id),
    invitee_id  UUID         NOT NULL REFERENCES users(id),
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    expires_at  TIMESTAMPTZ  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_game_invitee UNIQUE (game_id, invitee_id)
);

CREATE INDEX idx_game_invite_invitee_status ON game_invites (invitee_id, status);
CREATE INDEX idx_game_invite_expires_status ON game_invites (expires_at, status);
