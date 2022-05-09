ALTER TABLE rider_location
ADD COLUMN IF NOT EXISTS rider_status character varying(25);

ALTER TABLE rider_location
ADD COLUMN IF NOT EXISTS profile_status character varying(25);

ALTER TABLE rider_location
ADD COLUMN IF NOT EXISTS evbike_user boolean;

ALTER TABLE rider_location
ADD COLUMN IF NOT EXISTS renting_today boolean;

ALTER TABLE rider_location
ADD COLUMN IF NOT EXISTS preferred_zone character varying(25);