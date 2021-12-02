set search_path=public,intimev2;
begin;
-- define rules for anonymous user by defining all open data sets
--     stationtype     |                    origin       | period             
---------------------+----------------------------------------------
-- Bicycle             | ALGORAB
-- Bicycle             | BIKE_SHARING_MERANO
-- BikesharingStation  | ALGORAB
-- BluetoothStation    |
-- CarpoolingHub       | FLOOTA
-- CarpoolingService   | FLOOTA
-- CarpoolingUser      | FLOOTA
-- CarsharingCar       | HAL-API
-- CarsharingStation   | CARSHARINGBZ
-- CarsharingStation   | HAL-API
-- Covid-19            | provincebz
-- CreativeIndustry    | 1I5Zj7JHprwLhzl9ktXJO-7v7x3rh1ysGbiblZgZYCXU
-- EChargingPlug       | Nevicam
-- EChargingPlug       | DRIWE
-- EChargingPlug       | route220
-- EChargingPlug       | ALPERIA
-- EChargingPlug       |
-- EChargingPlug       | IIT
-- EChargingStation    | IIT
-- EChargingStation    | Nevicam
-- EChargingStation    |
-- EChargingStation    | route220
-- EChargingStation    | ALPERIA
-- EChargingStation    | DRIWE
-- EnvironmentStation  | APPABZ
-- EnvironmentStation  | FAMAS-traffic
-- EnvironmentStation  | APPATN-open
-- EnvironmentStation  | APPATN
-- LinkStation         |
-- MeteoStation        | SIAG
-- MeteoStation        | FAMAS-traffic
-- MeteoStation        | meteotrentino
-- Mobilestation       |
-- NOI-Place           | 1SSXusoMlNpQd-_CtKjft2Zh2yaWhoqNes4GzZl_X0GI
-- ParkingSensor       | AXIANS
-- ParkingStation      | FAMAS
-- ParkingStation      | Municipality Merano
-- ParkingStation      | FBK
-- RWISstation         | InfoMobility
-- Streetstation       |
-- traffic             |
-- TrafficSensor       | FAMAS-traffic
-- TrafficSensor       | A22
-- Trafficstation      |
-- TrafficStreetFactor |
-- VMS                 | A22

delete from bdprules ru where ru.role_id in (select id from bdprole where name = 'GUEST');
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'GUEST' and s.stationtype in ('Bicycle','BikesharingStation','BluetoothStation','CarpoolingHub','CarpoolingService','CarpoolingUser','CarsharingCar','CarsharingStation','EChargingPlug','EChargingStation','EnvironmentStation', 'LinkStation','MeteoStation','Mobilestation','ParkingSensor','ParkingStation','RWISstation','Streetstation') and (s.origin is null or s.origin in('ALGORAB','FLOOTA','HAL-API','CARSHARINGBZ','DRIWE','ALPERIA','IIT','route220','Nevicam','APPATN-open','meteotrentino','SIAG','FAMAS','Municipality Merano','FBK','InfoMobility','BIKE_SHARING_MERANO','AXIANS'));
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'GUEST' and s.stationtype in ('NOI-Place','CreativeIndustry');
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'GUEST' and s.stationtype = 'LinkStation' and s.origin = 'NOI';
insert into bdprules (role_id, station_id, period) select r.id, s.id, 3600 from station s, bdprole r where r.name = 'GUEST' and s.origin = 'APPABZ';

-- define rules for cbz user by making him inherit guest rules and adding specific stations
update bdprole set parent_id = (select id from bdprole where name = 'GUEST') where id in (select id from bdprole where name = 'CBZ');
delete from bdprules ru where ru.role_id in (select id from bdprole where name = 'CBZ');
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'CBZ' and s.stationtype = 'Mobilestation';
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'CBZ' and s.stationtype = 'TrafficSensor' and s.origin = 'FAMAS-traffic';

-- define rules for developers working on analytics mobility
update bdprole set parent_id = (select id from bdprole where name = 'GUEST') where id in (select id from bdprole where name = 'MAD');
delete from bdprules ru where ru.role_id in (select id from bdprole where name = 'MAD');
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'MAD' and s.stationtype in ('Bicycle','BikesharingStation','BluetoothStation','CarpoolingHub','CarpoolingService','CarpoolingUser','CarsharingCar','CarsharingStation','Covid-19','EChargingPlug','EChargingStation','EnvironmentStation','LinkStation','MeteoStation','Mobilestation','ParkingStation','RWISstation','Streetstation','traffic','TrafficSensor','Trafficstation','TrafficStreetFactor','VMS');

-- define rules for brennerlec user by making him inherit guest rules and adding specific stations
update bdprole set parent_id = (select id from bdprole where name = 'GUEST') where id in (select id from bdprole where name = 'BLC');
delete from bdprules ru where ru.role_id in (select id from bdprole where name = 'BLC');
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'BLC' and s.origin = 'A22';
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'BLC' and s.origin = 'a22-algorab';
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'BLC' and s.origin = 'APPABZ';
insert into bdprules (role_id, station_id) select r.id, s.id from station s, bdprole r where r.name = 'BLC' and s.origin = 'APPATN';


refresh materialized view bdppermissions;
commit;
