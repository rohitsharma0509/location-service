ALTER TABLE rider_location
ADD COLUMN IF NOT EXISTS food_box_size character varying(25) DEFAULT 'SMALL';