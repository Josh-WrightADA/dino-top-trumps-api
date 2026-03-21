CREATE TABLE reports (
    id               UUID PRIMARY KEY,
    reporter_id      UUID         NOT NULL REFERENCES users(id),
    reported_user_id UUID         NOT NULL REFERENCES users(id),
    reason           VARCHAR(500) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_reports_status ON reports (status);
CREATE INDEX idx_reports_reported_user ON reports (reported_user_id);
