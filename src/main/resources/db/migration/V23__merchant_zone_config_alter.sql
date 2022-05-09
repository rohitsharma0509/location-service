INSERT INTO province (name)
     (SELECT DISTINCT TRIM(split_part(name, ':', 1)) from bangkok_zone);

--segregating bangkok_zone.name col into name and province_id col with province_id from province col--
UPDATE bangkok_zone b
SET province_id = p.province_id,
    name = TRIM(split_part(b.name, ':', 2))
FROM province p
WHERE p.name = split_part(b.name, ':', 1);
