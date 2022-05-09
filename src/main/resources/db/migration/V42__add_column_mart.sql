ALTER TABLE rider_location
ADD COLUMN IF NOT EXISTS is_mart_rider boolean DEFAULT false;