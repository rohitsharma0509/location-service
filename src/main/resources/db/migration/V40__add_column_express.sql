ALTER TABLE rider_location
ADD COLUMN IF NOT EXISTS is_express_rider boolean DEFAULT false;