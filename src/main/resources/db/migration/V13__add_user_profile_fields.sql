-- Add bio and favourite card to user profiles
ALTER TABLE users ADD COLUMN bio VARCHAR(500);
ALTER TABLE users ADD COLUMN favourite_card_id UUID REFERENCES cards(id);
