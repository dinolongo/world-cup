-- Insert match data
-- This migration inserts World Cup 2026 match data

-- Insert matches with team ID lookups based on external_api_id
INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537327, (SELECT id FROM teams WHERE external_api_id = 769), (SELECT id FROM teams WHERE external_api_id = 774), '2026-06-11T19:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_A', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537328, (SELECT id FROM teams WHERE external_api_id = 772), (SELECT id FROM teams WHERE external_api_id = 798), '2026-06-12T02:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_A', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537333, (SELECT id FROM teams WHERE external_api_id = 828), (SELECT id FROM teams WHERE external_api_id = 1060), '2026-06-12T19:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_B', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537345, (SELECT id FROM teams WHERE external_api_id = 771), (SELECT id FROM teams WHERE external_api_id = 761), '2026-06-13T01:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_D', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537334, (SELECT id FROM teams WHERE external_api_id = 8030), (SELECT id FROM teams WHERE external_api_id = 788), '2026-06-13T19:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_B', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537339, (SELECT id FROM teams WHERE external_api_id = 764), (SELECT id FROM teams WHERE external_api_id = 815), '2026-06-13T22:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_C', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537340, (SELECT id FROM teams WHERE external_api_id = 836), (SELECT id FROM teams WHERE external_api_id = 8873), '2026-06-14T01:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_C', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537346, (SELECT id FROM teams WHERE external_api_id = 779), (SELECT id FROM teams WHERE external_api_id = 803), '2026-06-14T04:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_D', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537351, (SELECT id FROM teams WHERE external_api_id = 759), (SELECT id FROM teams WHERE external_api_id = 9460), '2026-06-14T17:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_E', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537357, (SELECT id FROM teams WHERE external_api_id = 8601), (SELECT id FROM teams WHERE external_api_id = 766), '2026-06-14T20:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_F', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537352, (SELECT id FROM teams WHERE external_api_id = 1935), (SELECT id FROM teams WHERE external_api_id = 791), '2026-06-14T23:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_E', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537358, (SELECT id FROM teams WHERE external_api_id = 792), (SELECT id FROM teams WHERE external_api_id = 802), '2026-06-15T02:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_F', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537369, (SELECT id FROM teams WHERE external_api_id = 760), (SELECT id FROM teams WHERE external_api_id = 1930), '2026-06-15T16:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_H', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537363, (SELECT id FROM teams WHERE external_api_id = 805), (SELECT id FROM teams WHERE external_api_id = 825), '2026-06-15T19:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_G', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537370, (SELECT id FROM teams WHERE external_api_id = 801), (SELECT id FROM teams WHERE external_api_id = 758), '2026-06-15T22:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_H', NOW())
ON CONFLICT (external_api_id) DO NOTHING;

INSERT INTO matches (external_api_id, home_team_id, away_team_id, utc_date, status, home_score, away_score, stage, group_name, last_updated)
VALUES 
(537364, (SELECT id FROM teams WHERE external_api_id = 840), (SELECT id FROM teams WHERE external_api_id = 783), '2026-06-16T01:00:00Z', 'TIMED', NULL, NULL, 'GROUP_STAGE', 'GROUP_G', NOW())
ON CONFLICT (external_api_id) DO NOTHING;
