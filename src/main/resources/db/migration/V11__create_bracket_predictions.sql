-- Create bracket_predictions table
CREATE TABLE IF NOT EXISTS bracket_predictions (
    bracket_id              CHAR(8)     PRIMARY KEY,
    display_name            VARCHAR(50) NOT NULL,
    group_stage_predictions JSONB       NOT NULL,
    knockout_predictions    JSONB       NOT NULL,
    total_score             INTEGER,
    created_at              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_bracket_display_name ON bracket_predictions(display_name);
CREATE INDEX IF NOT EXISTS idx_bracket_created_at ON bracket_predictions(created_at);
