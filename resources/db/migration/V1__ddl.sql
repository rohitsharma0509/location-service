CREATE EXTENSION IF NOT EXISTS POSTGIS;

CREATE TABLE IF NOT EXISTS bangkok_zone
(
    zone_id integer NOT NULL,
    geom geometry,
    postal_code integer,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT bangkok_zone_pkey PRIMARY KEY (zone_id)
);

CREATE TABLE IF NOT EXISTS rider_location
(
    rider_id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    geom geometry,
    updated_timestamp TIMESTAMP, 
    CONSTRAINT rider_location_pkey PRIMARY KEY (rider_id)
);

CREATE TABLE IF NOT EXISTS rider_zone
(
    rider_id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    zone_id integer,
    CONSTRAINT rider_zone_pkey PRIMARY KEY (rider_id)
)
