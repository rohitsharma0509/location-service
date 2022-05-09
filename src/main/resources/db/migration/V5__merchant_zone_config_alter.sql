ALTER TABLE bangkok_zone
ADD COLUMN IF NOT EXISTS max_jobs_for_rider integer,
ADD COLUMN IF NOT EXISTS max_riders_for_job integer;

UPDATE bangkok_zone
SET max_jobs_for_rider = 3 ;


UPDATE bangkok_zone
SET max_riders_for_job = 15 ;