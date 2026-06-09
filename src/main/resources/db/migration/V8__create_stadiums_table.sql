-- Create stadiums table
CREATE TABLE IF NOT EXISTS stadiums (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    city VARCHAR(255) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    timezone VARCHAR(10) NOT NULL,
    capacity INTEGER NOT NULL,
    coordinates VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on city for faster lookups
CREATE INDEX idx_stadiums_city ON stadiums(city);

-- Insert stadium data
INSERT INTO stadiums (name, city, country_code, timezone, capacity, coordinates) VALUES
('BC Place', 'Vancouver', 'CA', 'UTC-7', 54000, '49°16''36"N 123°6''43"W'),
('Lumen Field', 'Seattle', 'US', 'UTC-7', 69000, '47°35''43"N 122°19''54"W'),
('Levi''s Stadium', 'San Francisco Bay Area', 'US', 'UTC-7', 71000, '37.403°N 121.970°W'),
('SoFi Stadium', 'Los Angeles', 'US', 'UTC-7', 70000, '33.953°N 118.339°W'),
('Estadio Akron', 'Guadalajara', 'MX', 'UTC-6', 48000, '20°40''54"N 103°27''46"W'),
('Estadio Azteca', 'Mexico City', 'MX', 'UTC-6', 83000, '19°18''11"N 99°09''02"W'),
('Estadio BBVA', 'Monterrey', 'MX', 'UTC-6', 53500, '25°40''9"N 100°14''40"W'),
('NRG Stadium', 'Houston', 'US', 'UTC-5', 72000, '29°41''5"N 95°24''39"W'),
('AT&T Stadium', 'Dallas', 'US', 'UTC-5', 94000, '32°44''52"N 97°5''34"W'),
('Arrowhead Stadium', 'Kansas City', 'US', 'UTC-5', 73000, '39°2''56"N 94°29''2"W'),
('Mercedes-Benz Stadium', 'Atlanta', 'US', 'UTC-4', 75000, '33°45''20"N 84°24''00"W'),
('Hard Rock Stadium', 'Miami', 'US', 'UTC-4', 65000, '25°57''29"N 80°14''20"W'),
('BMO Field', 'Toronto', 'CA', 'UTC-4', 45000, '43°38''0"N 79°25''07"W'),
('Gillette Stadium', 'Boston', 'US', 'UTC-4', 65000, '42.091°N 71.264°W'),
('Lincoln Financial Field', 'Philadelphia', 'US', 'UTC-4', 69000, '39°54''3"N 75°10''3"W'),
('MetLife Stadium', 'New York/New Jersey', 'US', 'UTC-4', 82500, '40°48''48.7"N 74°4''27.7"W')
ON CONFLICT (name) DO NOTHING;
