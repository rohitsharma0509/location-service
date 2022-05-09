
-- renaming Bangkok Noi
update bangkok_zone set name='กรุงเทพฯ: บางกอกน้อย' where zone_id=22;

-- renaming Bangkok Yai
update bangkok_zone set name='กรุงเทพฯ: บางกอกใหญ่' where zone_id=28;

-- deleting Chiang Mai, Nakhon Si Thammarat, Chon Buri
DELETE FROM bangkok_zone WHERE zone_id = 56 and name = 'กรุงเทพฯ: เชียงใหม่';
DELETE FROM bangkok_zone WHERE zone_id = 57 and name = 'กรุงเทพฯ: นครศรีธรรมราช';
DELETE FROM bangkok_zone WHERE zone_id = 58 and name = 'กรุงเทพฯ: ชลบุรี';

-- updating Samut Sakhon - Mueang as zone 78
UPDATE public.bangkok_zone SET zone_id=56 WHERE zone_id = 78;

-- updating Samut Sakhon - Krathum Baen as zone 79
UPDATE public.bangkok_zone SET zone_id=57 WHERE zone_id = 79;

-- updating Nakhon Ratchasima - Mueang as zone 80
UPDATE public.bangkok_zone SET zone_id=58 WHERE zone_id = 80;	

