DELETE FROM intime.schemaversion;
INSERT INTO intime.schemaversion VALUES('1.3.6');

ALTER TABLE intime.alarm RENAME COLUMN createdate TO create_date;

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
