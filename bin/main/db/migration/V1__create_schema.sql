-- Create teams table
CREATE TABLE IF NOT EXISTS teams (
    id BIGSERIAL PRIMARY KEY,
    external_api_id INTEGER UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    short_name VARCHAR(100),
    tla VARCHAR(3),
    crest_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create matches table
CREATE TABLE IF NOT EXISTS matches (
    id BIGSERIAL PRIMARY KEY,
    external_api_id INTEGER UNIQUE NOT NULL,
    home_team_id BIGINT NOT NULL,
    away_team_id BIGINT NOT NULL,
    utc_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    home_score INTEGER,
    away_score INTEGER,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_home_team FOREIGN KEY (home_team_id) REFERENCES teams(id),
    CONSTRAINT fk_away_team FOREIGN KEY (away_team_id) REFERENCES teams(id)
);

-- Create group_standings table
CREATE TABLE IF NOT EXISTS group_standings (
    id BIGSERIAL PRIMARY KEY,
    group_name VARCHAR(10) NOT NULL,
    team_id BIGINT NOT NULL,
    position INTEGER,
    played_games INTEGER,
    wins INTEGER,
    draws INTEGER,
    losses INTEGER,
    goals_for INTEGER,
    goals_against INTEGER,
    goal_difference INTEGER,
    points INTEGER,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_team FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT uk_group_team UNIQUE (group_name, team_id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_team_external_api_id ON teams(external_api_id);
CREATE INDEX IF NOT EXISTS idx_team_tla ON teams(tla);

CREATE INDEX IF NOT EXISTS idx_match_external_api_id ON matches(external_api_id);
CREATE INDEX IF NOT EXISTS idx_match_home_team ON matches(home_team_id);
CREATE INDEX IF NOT EXISTS idx_match_away_team ON matches(away_team_id);
CREATE INDEX IF NOT EXISTS idx_match_status ON matches(status);
CREATE INDEX IF NOT EXISTS idx_match_utc_date ON matches(utc_date);

CREATE INDEX IF NOT EXISTS idx_standing_group ON group_standings(group_name);
CREATE INDEX IF NOT EXISTS idx_standing_team ON group_standings(team_id);
CREATE INDEX IF NOT EXISTS idx_standing_group_team ON group_standings(group_name, team_id);
