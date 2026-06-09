-- Add group_name column to teams table
ALTER TABLE teams ADD COLUMN IF NOT EXISTS group_name VARCHAR(10);

-- Create index on group_name column
CREATE INDEX IF NOT EXISTS idx_team_group ON teams(group_name);
