BEGIN;

DELETE FROM intime.schemaversion;
INSERT INTO intime.schemaversion VALUES('1.3.6');

ALTER TABLE intime.bikesharingstationbasicdata RENAME TO bikesharing_station_basic_data;
ALTER TABLE intime.carpoolinguserbasicdata RENAME TO carpooling_user_basic_data;
ALTER TABLE intime.carpoolinguserbasicdata_translation RENAME TO carpooling_user_basic_data_translation;
ALTER TABLE intime.carpoolinghubbasicdata RENAME TO carpoolinghub_basic_data;
ALTER TABLE intime.carpoolinghubbasicdata_translation RENAME TO carpoolinghub_basic_data_translation;
ALTER TABLE intime.carsharingcarstationbasicdata RENAME TO carsharing_car_station_basic_data;
ALTER TABLE intime.carsharingstationbasicdata RENAME TO carsharing_station_basic_data;
ALTER TABLE intime.datatype_i18n RENAME TO data_type_i18n;
ALTER TABLE intime.echargingplugbasicdata RENAME TO echarging_plug_basic_data;
ALTER TABLE intime.echargingplugoutlet RENAME TO echarging_plug_outlet;
ALTER TABLE intime.echargingstationbasicdata RENAME TO echargingstation_basic_data;
ALTER TABLE intime.elaborationhistory RENAME TO elaboration_history;

ALTER TABLE intime.alarm RENAME COLUMN createdate TO create_date;

ALTER TABLE intime.bicyclebasicdata RENAME COLUMN bikesharingstation_id TO bike_sharing_station_id;

ALTER TABLE intime.echargingplugoutlet RENAME COLUMN maxcurrent TO max_current;
ALTER TABLE intime.echargingplugoutlet RENAME COLUMN mincurrent TO min_current;
ALTER TABLE intime.echargingplugoutlet RENAME COLUMN maxpower TO max_power;
ALTER TABLE intime.echargingplugoutlet RENAME COLUMN plugtype TO plug_type;
ALTER TABLE intime.echargingplugoutlet RENAME COLUMN hasfixedcable TO has_fixed_cable;

ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN assetprovider TO asset_provider;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN chargingpointscount TO charging_points_count;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN paymentinfo TO payment_info;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN accessinfo TO access_info;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN flashinfo TO flash_info;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN locationserviceinfo TO location_service_info;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN accesstype TO access_type;

ALTER TABLE intime.carsharingcarstationbasicdata RENAME COLUMN licenseplate TO license_plate;

ALTER TABLE intime.carsharingstationbasicdata RENAME COLUMN hasfixedparking TO has_fixed_parking;
ALTER TABLE intime.carsharingstationbasicdata RENAME COLUMN canbookahead TO can_book_ahead;
ALTER TABLE intime.carsharingstationbasicdata RENAME COLUMN companyshortname TO company_short_name;

ALTER TABLE intime.carpoolinguserbasicdata RENAME COLUMN cartype TO car_type;

ALTER TABLE intime.bdpuser RENAME COLUMN tokenexpired TO token_expired;

COMMIT;


BEGIN;

DELETE FROM intime.schemaversion;
INSERT INTO intime.schemaversion VALUES('1.3.5');

ALTER TABLE intime.alarm RENAME COLUMN create_date TO createdate;

ALTER TABLE intime.bicyclebasicdata RENAME COLUMN bike_sharing_station_id TO bikesharingstation_id;

ALTER TABLE intime.echargingplugoutlet RENAME COLUMN max_current TO maxcurrent;
ALTER TABLE intime.echargingplugoutlet RENAME COLUMN min_current TO mincurrent;
ALTER TABLE intime.echargingplugoutlet RENAME COLUMN max_power TO maxpower;
ALTER TABLE intime.echargingplugoutlet RENAME COLUMN plug_type TO plugtype;
ALTER TABLE intime.echargingplugoutlet RENAME COLUMN has_fixed_cable TO hasfixedcable;

ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN asset_provider TO assetprovider;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN charging_points_count TO chargingpointscount;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN payment_info TO paymentinfo;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN access_info TO accessinfo;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN flash_info TO flashinfo;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN location_service_info TO locationserviceinfo;
ALTER TABLE intime.echargingstationbasicdata RENAME COLUMN access_type TO accesstype;

ALTER TABLE intime.carsharingcarstationbasicdata RENAME COLUMN license_plate TO licenseplate;

ALTER TABLE intime.carsharingstationbasicdata RENAME COLUMN has_fixed_parking TO hasfixedparking;
ALTER TABLE intime.carsharingstationbasicdata RENAME COLUMN can_book_ahead TO canbookahead;
ALTER TABLE intime.carsharingstationbasicdata RENAME COLUMN company_short_name TO companyshortname;

ALTER TABLE intime.carpoolinguserbasicdata RENAME COLUMN car_type TO cartype;

ALTER TABLE intime.bdpuser RENAME COLUMN token_expired TO tokenexpired;
COMMIT;