ALTER TABLE rider_location
ADD COLUMN IF NOT EXISTS is_pointx_rider boolean DEFAULT false;