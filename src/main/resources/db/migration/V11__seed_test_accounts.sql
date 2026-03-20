-- Seed test accounts for manual testing
-- Password for all test accounts: password123
-- BCrypt hash: $2a$10$.tek.BXw2gkhhUOBTtnsK.LBe/W1CrLXTpbGiBsc22TCHRmD4lrdK

INSERT INTO users (id, username, email, password_hash, display_name, avatar_url, elo_rating, games_played, games_won, created_at, updated_at)
SELECT '00000000-0000-0000-0000-000000000001', 'player1', 'player1@test.com', '$2a$10$.tek.BXw2gkhhUOBTtnsK.LBe/W1CrLXTpbGiBsc22TCHRmD4lrdK', 'Player One', '', 1000, 0, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'player1');

INSERT INTO users (id, username, email, password_hash, display_name, avatar_url, elo_rating, games_played, games_won, created_at, updated_at)
SELECT '00000000-0000-0000-0000-000000000002', 'player2', 'player2@test.com', '$2a$10$.tek.BXw2gkhhUOBTtnsK.LBe/W1CrLXTpbGiBsc22TCHRmD4lrdK', 'Player Two', '', 1000, 0, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'player2');

INSERT INTO users (id, username, email, password_hash, display_name, avatar_url, elo_rating, games_played, games_won, created_at, updated_at)
SELECT '00000000-0000-0000-0000-000000000003', 'player3', 'player3@test.com', '$2a$10$.tek.BXw2gkhhUOBTtnsK.LBe/W1CrLXTpbGiBsc22TCHRmD4lrdK', 'Player Three', '', 1000, 0, 0, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'player3');
