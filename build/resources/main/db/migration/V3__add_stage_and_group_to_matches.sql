-- Add stage column to matches table
ALTER TABLE matches ADD COLUMN IF NOT EXISTS stage VARCHAR(50);

-- Add group_name column to matches table
ALTER TABLE matches ADD COLUMN IF NOT EXISTS group_name VARCHAR(20);

-- Create index on stage column
CREATE INDEX IF NOT EXISTS idx_match_stage ON matches(stage);

-- Create index on group_name column
CREATE INDEX IF NOT EXISTS idx_match_group ON matches(group_name);
