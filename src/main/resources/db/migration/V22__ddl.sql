CREATE TABLE IF NOT EXISTS province
(
    province_id integer GENERATED ALWAYS AS IDENTITY,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT province_pkey PRIMARY KEY (province_id)
);

ALTER TABLE bangkok_zone
    ADD COLUMN IF NOT EXISTS province_id integer;

ALTER TABLE bangkok_zone
    ADD CONSTRAINT province_fk
    FOREIGN KEY (province_id)
    REFERENCES province (province_id)
    ON DELETE NO ACTION;

-- ALTER TABLE bangkok_zone RENAME TO zone;
