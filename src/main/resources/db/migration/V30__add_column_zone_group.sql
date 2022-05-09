ALTER TABLE bangkok_zone
ADD COLUMN IF NOT EXISTS zone_group integer DEFAULT 100;
