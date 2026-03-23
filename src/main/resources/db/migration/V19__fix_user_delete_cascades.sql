-- Fix account deletion: add ON DELETE CASCADE to user-referencing FKs
-- Without this, deleting a user with games/friends/reports fails with FK violation

-- Games: drop and recreate FK constraints with CASCADE
ALTER TABLE games DROP CONSTRAINT IF EXISTS games_player1_id_fkey;
ALTER TABLE games DROP CONSTRAINT IF EXISTS games_player2_id_fkey;
ALTER TABLE games DROP CONSTRAINT IF EXISTS games_winner_id_fkey;

ALTER TABLE games ADD CONSTRAINT games_player1_id_fkey
    FOREIGN KEY (player1_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE games ADD CONSTRAINT games_player2_id_fkey
    FOREIGN KEY (player2_id) REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE games ADD CONSTRAINT games_winner_id_fkey
    FOREIGN KEY (winner_id) REFERENCES users(id) ON DELETE SET NULL;

-- Turns: active_player and winner refs
ALTER TABLE turns DROP CONSTRAINT IF EXISTS turns_active_player_id_fkey;
ALTER TABLE turns DROP CONSTRAINT IF EXISTS turns_winner_player_id_fkey;

ALTER TABLE turns ADD CONSTRAINT turns_active_player_id_fkey
    FOREIGN KEY (active_player_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE turns ADD CONSTRAINT turns_winner_player_id_fkey
    FOREIGN KEY (winner_player_id) REFERENCES users(id) ON DELETE SET NULL;

-- Reports: cascade delete when either user is deleted
ALTER TABLE reports DROP CONSTRAINT IF EXISTS reports_reporter_id_fkey;
ALTER TABLE reports DROP CONSTRAINT IF EXISTS reports_reported_user_id_fkey;

ALTER TABLE reports ADD CONSTRAINT reports_reporter_id_fkey
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE reports ADD CONSTRAINT reports_reported_user_id_fkey
    FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Friendships: cascade delete when either user is deleted
ALTER TABLE friendships DROP CONSTRAINT IF EXISTS friendships_requester_id_fkey;
ALTER TABLE friendships DROP CONSTRAINT IF EXISTS friendships_addressee_id_fkey;

ALTER TABLE friendships ADD CONSTRAINT friendships_requester_id_fkey
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE friendships ADD CONSTRAINT friendships_addressee_id_fkey
    FOREIGN KEY (addressee_id) REFERENCES users(id) ON DELETE CASCADE;

-- Game invites: cascade delete when either user is deleted
ALTER TABLE game_invites DROP CONSTRAINT IF EXISTS game_invites_inviter_id_fkey;
ALTER TABLE game_invites DROP CONSTRAINT IF EXISTS game_invites_invitee_id_fkey;

ALTER TABLE game_invites ADD CONSTRAINT game_invites_inviter_id_fkey
    FOREIGN KEY (inviter_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE game_invites ADD CONSTRAINT game_invites_invitee_id_fkey
    FOREIGN KEY (invitee_id) REFERENCES users(id) ON DELETE CASCADE;
