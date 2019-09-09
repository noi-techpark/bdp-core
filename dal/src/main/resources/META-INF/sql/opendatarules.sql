set search_path=public,intimev2;
begin;
-- define rules for anonymous user by excluding all non opendata sets
delete from bdprules ru  where ru.role_id in (select id from bdprole where name='GUEST');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'GUEST' and s.stationtype not in ('TrafficSensor','MobileStation','VMS') and (s.origin is null or s.origin not in('FAMAS-traffic','APPATN','A22–algorab'));
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'GUEST' and s.origin='A22-traffic' and s.stationtype='Linkstation';

update bdprules set period = 3600 where station_id in (select id from station where origin = 'APPABZ') and role_id=(select id from bdprole where name='GUEST');

-- define rules for cbz user by making him inherit guest rules and adding specific stations
update bdprole set parent_id = (select id from bdprole where name='GUEST') where id in (select id from bdprole where name='CBZ');
delete from bdprules ru  where ru.role_id in (select id from bdprole where name='CBZ');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'CBZ' and (s.stationtype='Mobilestation');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'CBZ' and (s.stationtype='TrafficSensor' and s.origin='FAMAS-traffic');


-- define rules for developers working on analytics mobility
delete from bdprules ru  where ru.role_id in (select id from bdprole where name='MAD');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'MAD' and s.stationtype in ('Bicycle','BikesharingStation','BluetoothStation','CarpoolingHub','CarpoolingService','CarpoolingUser','CarsharingCar','CarsharingStation','EChargingPlug','EChargingStation','EnvironmentStation','Linkstation','MeteoStation','Mobilestation','ParkingStation','RWISstation','Streetstation','traffic','TrafficSensor','Trafficstation','TrafficStreetFactor','VMS');

-- define rules for brennerlec user by making him inherit guest rules and adding specific stations
update bdprole set parent_id = (select id from bdprole where name='GUEST') where id in (select id from bdprole where name='BLC');
delete from bdprules ru  where ru.role_id in (select id from bdprole where name='BLC');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'BLC' and s.stationcode = 'APPATN_27';
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'BLC' and s.origin in ('APPABZ','A22-algorab');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'BLC' and s.origin = 'A22-traffic' and stationtype ='TrafficSensor';
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'BLC' and s.origin = 'A22-traffic' and stationtype ='RWISstation';
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'BLC' and s.origin = 'A22-traffic' and stationtype ='VMS';


refresh materialized view bdppermissions;
commit;
