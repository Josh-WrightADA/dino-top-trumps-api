CREATE TABLE friendships (
    id            UUID PRIMARY KEY,
    requester_id  UUID         NOT NULL REFERENCES users(id),
    addressee_id  UUID         NOT NULL REFERENCES users(id),
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_friendship UNIQUE (requester_id, addressee_id),
    CONSTRAINT chk_no_self_friend CHECK (requester_id != addressee_id)
);

CREATE INDEX idx_friendships_addressee_status ON friendships (addressee_id, status);
CREATE INDEX idx_friendships_requester_status ON friendships (requester_id, status);
