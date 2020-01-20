set search_path=public,intimev2;
begin;
-- define rules for anonymous user by defining all open data sets
--     stationtype     |                    origin       | period             
---------------------+----------------------------------------------
-- Bicycle             | ALGORAB
-- BikesharingStation  | ALGORAB
-- BluetoothStation    | 
-- CarpoolingHub       | FLOOTA
-- CarpoolingService   | FLOOTA
-- CarpoolingUser      | FLOOTA
-- CarsharingCar       | HAL-API
-- CarsharingStation   | CARSHARINGBZ
-- CarsharingStation   | HAL-API
-- EChargingPlug       | DRIWE
-- EChargingPlug       | ALPERIA
-- EChargingPlug       | 
-- EChargingPlug       | IIT
-- EChargingPlug       | route220
-- EChargingPlug       | Nevicam
-- EChargingStation    | Nevicam
-- EChargingStation    | 
-- EChargingStation    | IIT
-- EChargingStation    | ALPERIA
-- EChargingStation    | DRIWE
-- EChargingStation    | route220
-- EnvironmentStation  | APPATN-open
-- EnvironmentStation  | APPABZ | 3600
-- LinkStation         | 
-- MeteoStation        | meteotrentino
-- MeteoStation        | SIAG
-- Mobilestation       | 
-- ParkingStation      | FAMAS
-- ParkingStation      | Municipality Merano
-- ParkingStation      | FBK
-- RWISstation         | InfoMobility
-- Streetstation       | 
delete from bdprules ru  where ru.role_id in (select id from bdprole where name='GUEST');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'GUEST' and s.stationtype in ('Bicycle','BikesharingStation','BluetoothStation','CarpoolingHub','CarpoolingService','CarpoolingUser','CarsharingCar','CarsharingStation','EChargingPlug','EChargingStation','EnvironmentStation', 'LinkStation','MeteoStation','Mobilestation','ParkingStation','RWISstation','Streetstation') and (s.origin is null or s.origin in('ALGORAB','FLOOTA','HAL-API','CARSHARINGBZ','DRIWE','ALPERIA','IIT','route220','Nevicam','APPATN-open','meteotrentino','SIAG','FAMAS','Municipality Merano','FBK','InfoMobility','APPABZ'));

update bdprules set period = 3600 where station_id in (select id from station where origin = 'APPABZ') and role_id=(select id from bdprole where name='GUEST');

-- define rules for cbz user by making him inherit guest rules and adding specific stations
update bdprole set parent_id = (select id from bdprole where name='GUEST') where id in (select id from bdprole where name='CBZ');
delete from bdprules ru  where ru.role_id in (select id from bdprole where name='CBZ');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'CBZ' and (s.stationtype='Mobilestation');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'CBZ' and (s.stationtype='TrafficSensor' and s.origin='FAMAS-traffic');


-- define rules for developers working on analytics mobility
delete from bdprules ru  where ru.role_id in (select id from bdprole where name='MAD');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'MAD' and s.stationtype in ('Bicycle','BikesharingStation','BluetoothStation','CarpoolingHub','CarpoolingService','CarpoolingUser','CarsharingCar','CarsharingStation','EChargingPlug','EChargingStation','EnvironmentStation','LinkStation','MeteoStation','Mobilestation','ParkingStation','RWISstation','Streetstation','traffic','TrafficSensor','Trafficstation','TrafficStreetFactor','VMS');

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
