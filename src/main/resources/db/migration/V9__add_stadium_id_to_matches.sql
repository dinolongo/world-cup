-- Add stadium_id foreign key to matches table
ALTER TABLE matches ADD COLUMN stadium_id BIGINT;

-- Add foreign key constraint
ALTER TABLE matches 
ADD CONSTRAINT fk_match_stadium 
FOREIGN KEY (stadium_id) REFERENCES stadiums(id);

-- Add index for faster lookups
CREATE INDEX idx_match_stadium ON matches(stadium_id);
