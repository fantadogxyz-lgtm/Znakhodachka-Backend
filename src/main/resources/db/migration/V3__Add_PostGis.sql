CREATE EXTENSION IF NOT EXISTS postgis;

ALTER TABLE publications.publications
    ADD COLUMN location geography(POINT, 4326);

UPDATE publications.publications
SET location = ST_MakePoint(longitude, latitude)::geography
WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

CREATE INDEX idx_publications_location
    ON publications.publications USING GIST(location);