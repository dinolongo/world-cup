-- Insert teams data
-- This migration inserts the 48 World Cup 2026 teams

BEGIN;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (758, 'Uruguay', 'Uruguay', 'URY', 'https://crests.football-data.org/758.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (759, 'Germany', 'Germany', 'GER', 'https://crests.football-data.org/759.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (760, 'Spain', 'Spain', 'ESP', 'https://crests.football-data.org/760.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (761, 'Paraguay', 'Paraguay', 'PAR', 'https://crests.football-data.org/761.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (762, 'Argentina', 'Argentina', 'ARG', 'https://crests.football-data.org/762.png')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (763, 'Ghana', 'Ghana', 'GHA', 'https://crests.football-data.org/ghana.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (764, 'Brazil', 'Brazil', 'BRA', 'https://crests.football-data.org/764.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (765, 'Portugal', 'Portugal', 'POR', 'https://crests.football-data.org/765.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (766, 'Japan', 'Japan', 'JPN', 'https://crests.football-data.org/766.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (769, 'Mexico', 'Mexico', 'MEX', 'https://crests.football-data.org/769.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (770, 'England', 'England', 'ENG', 'https://crests.football-data.org/770.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (771, 'United States', 'USA', 'USA', 'https://crests.football-data.org/usa.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (772, 'South Korea', 'Korea Republic', 'KOR', 'https://crests.football-data.org/772.png')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (773, 'France', 'France', 'FRA', 'https://crests.football-data.org/773.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (774, 'South Africa', 'South Africa', 'RSA', 'https://crests.football-data.org/9396.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (778, 'Algeria', 'Algeria', 'ALG', 'https://crests.football-data.org/algeria.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (779, 'Australia', 'Australia', 'AUS', 'https://crests.football-data.org/779.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (783, 'New Zealand', 'New Zealand', 'NZL', 'https://crests.football-data.org/783.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (788, 'Switzerland', 'Switzerland', 'SUI', 'https://crests.football-data.org/788.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (791, 'Ecuador', 'Ecuador', 'ECU', 'https://crests.football-data.org/791.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (792, 'Sweden', 'Sweden', 'SWE', 'https://crests.football-data.org/792.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (798, 'Czechia', 'Czechia', 'CZE', 'https://crests.football-data.org/798.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (799, 'Croatia', 'Croatia', 'CRO', 'https://crests.football-data.org/799.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (801, 'Saudi Arabia', 'Saudi Arabia', 'KSA', 'https://crests.football-data.org/saudi_arabia.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (802, 'Tunisia', 'Tunisia', 'TUN', 'https://crests.football-data.org/tunisia.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (803, 'Turkey', 'Turkey', 'TUR', 'https://crests.football-data.org/803.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (804, 'Senegal', 'Senegal', 'SEN', 'https://crests.football-data.org/senegal.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (805, 'Belgium', 'Belgium', 'BEL', 'https://crests.football-data.org/805.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (815, 'Morocco', 'Morocco', 'MAR', 'https://crests.football-data.org/morocco.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (816, 'Austria', 'Austria', 'AUT', 'https://crests.football-data.org/816.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (818, 'Colombia', 'Colombia', 'COL', 'https://crests.football-data.org/818.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (825, 'Egypt', 'Egypt', 'EGY', 'https://crests.football-data.org/825.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (828, 'Canada', 'Canada', 'CAN', 'https://crests.football-data.org/canada.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (836, 'Haiti', 'Haiti', 'HAI', 'https://crests.football-data.org/haiti.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (840, 'Iran', 'Iran', 'IRN', 'https://crests.football-data.org/iran.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (1060, 'Bosnia-Herzegovina', 'Bosnia-H.', 'BIH', 'https://crests.football-data.org/bosnia.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (1836, 'Panama', 'Panama', 'PAN', 'https://crests.football-data.org/panama.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (1930, 'Cape Verde Islands', 'Cape Verde', 'CPV', 'https://crests.football-data.org/cape_verde.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (1934, 'Congo DR', 'Congo DR', 'COD', 'https://crests.football-data.org/congo_dr.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (1935, 'Ivory Coast', 'Ivory Coast', 'CIV', 'https://crests.football-data.org/787.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (8030, 'Qatar', 'Qatar', 'QAT', 'https://crests.football-data.org/8030.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (8049, 'Jordan', 'Jordan', 'JOR', 'https://crests.football-data.org/8049.png')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (8062, 'Iraq', 'Iraq', 'IRQ', 'https://crests.football-data.org/iraq.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (8070, 'Uzbekistan', 'Uzbekistan', 'UZB', 'https://crests.football-data.org/8070.png')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (8601, 'Netherlands', 'Netherlands', 'NED', 'https://crests.football-data.org/8601.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (8872, 'Norway', 'Norway', 'NOR', 'https://crests.football-data.org/813.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (8873, 'Scotland', 'Scotland', 'SCO', 'https://crests.football-data.org/814.svg')
ON CONFLICT (external_api_id) DO NOTHING;
INSERT INTO teams (external_api_id, name, short_name, tla, crest_url)
VALUES (9460, 'Curaçao', 'Curaçao', 'CUW', 'https://crests.football-data.org/curacao.svg')
ON CONFLICT (external_api_id) DO NOTHING;

COMMIT;