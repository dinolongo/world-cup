-- Populate team groups based on World Cup 2026 group assignments
BEGIN;

-- Group A
UPDATE teams SET group_name = 'GROUP_A' WHERE name IN ('Mexico', 'South Africa', 'South Korea', 'Czechia');

-- Group B
UPDATE teams SET group_name = 'GROUP_B' WHERE name IN ('Canada', 'Qatar', 'Switzerland', 'Bosnia and Herzegovina');

-- Group C
UPDATE teams SET group_name = 'GROUP_C' WHERE name IN ('Brazil', 'Morocco', 'Haiti', 'Scotland');

-- Group D
UPDATE teams SET group_name = 'GROUP_D' WHERE name IN ('United States', 'Paraguay', 'Australia', 'Turkey');

-- Group E
UPDATE teams SET group_name = 'GROUP_E' WHERE name IN ('Germany', 'Curaçao', 'Ivory Coast', 'Ecuador');

-- Group F
UPDATE teams SET group_name = 'GROUP_F' WHERE name IN ('Netherlands', 'Japan', 'Sweden', 'Tunisia');

-- Group G
UPDATE teams SET group_name = 'GROUP_G' WHERE name IN ('Belgium', 'Egypt', 'Iran', 'New Zealand');

-- Group H
UPDATE teams SET group_name = 'GROUP_H' WHERE name IN ('Spain', 'Cape Verde', 'Saudi Arabia', 'Uruguay');

-- Group I
UPDATE teams SET group_name = 'GROUP_I' WHERE name IN ('France', 'Senegal', 'Iraq', 'Norway');

-- Group J
UPDATE teams SET group_name = 'GROUP_J' WHERE name IN ('Argentina', 'Algeria', 'Austria', 'Jordan');

-- Group K
UPDATE teams SET group_name = 'GROUP_K' WHERE name IN ('Portugal', 'Uzbekistan', 'Colombia', 'DR Congo');

-- Group L
UPDATE teams SET group_name = 'GROUP_L' WHERE name IN ('England', 'Croatia', 'Ghana', 'Panama');

COMMIT;
