-- Performance indexes for frequently queried columns
-- Existing indexes (from V1/V2/V4/V5): users.username, users.email,
-- password_reset_tokens.token, password_reset_tokens.user_id,
-- games.player1_id, games.player2_id, games.status, turns.game_id

-- Leaderboard: ORDER BY elo_rating DESC
CREATE INDEX idx_users_elo_rating ON users (elo_rating DESC);

-- Stale game cleanup: WHERE status = ? AND created_at < ?
CREATE INDEX idx_games_status_created_at ON games (status, created_at);

-- Turn history: WHERE game_id = ? ORDER BY turn_number
CREATE INDEX idx_turns_game_id_turn_number ON turns (game_id, turn_number);
