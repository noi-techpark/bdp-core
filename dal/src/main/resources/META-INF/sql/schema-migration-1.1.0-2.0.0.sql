-------------------------------------------------------------------------------------------------------------
-- 								MIGRATION SCRIPT FROM v1.1.0 TO v2.0.0									   --
-------------------------------------------------------------------------------------------------------------
--
-- We do not change the schema step-by-step here, but create a new schema from scratch and import data.
-- This new schema is called "intimev2", the old schema is kept as "intime". After this script has run
-- successfully you should then finally rename the old schema to "intimev1" and the new schema "intimev2" to
-- "intime". Run it with schema-migration-1.1.0-2.0.0.sh script.
--
-- Run this script as follows:
-- PGPASSWORD=secr3t psql -v ON_ERROR_STOP=1 --echo-all -U <user> -p 5432 -h <host> -d <db> -f this-script.sql
--
-- To avoid data loss, always use ON_ERROR_STOP!
--
-- Author: p.moser@noi.bz.it
--
-------------------------------------------------------------------------------------------------------------

-- Make sure this script does not touch schemas other than v1.1.0
-- select * from intimev2.schemaversion;
-- delete from intimev2.schemaversion; insert into intimev2.schemaversion values('1.1.0');
DO language plpgsql $$
DECLARE
	count INT;
BEGIN
	SELECT COUNT(*) INTO count FROM intimev2.schemaversion WHERE version = '2.0.0';
	IF count <> 1 THEN
		RAISE EXCEPTION 'Wrong schema version... terminating script';
	END IF;
END
$$;


-------------------------------------------------------------------------------------------------------------
-- type
-------------------------------------------------------------------------------------------------------------
-- select * from intime.type where timestamp is not null
-- table intimev2.type;
insert into intimev2.type
select id, cname, created_on, cunit, description, rtype from intime.type;
select setval('intimev2.type_seq', (select max(id) from intimev2.type));

-------------------------------------------------------------------------------------------------------------
-- station
-------------------------------------------------------------------------------------------------------------
--table intimev2.station;
--select * from intime.station limit 10;
insert into intimev2.station
select id, active, available, name, origin, pointprojection, stationcode, stationtype, null, parent_id from intime.station;
select setval('intimev2.station_seq', (select max(id) from intimev2.station));

insert into intimev2.metadata (station_id, created_on, json)
select id
	   , now()
	   , jsonb_strip_nulls(jsonb_build_object(
		 'municipality', case when char_length(municipality) > 0 then municipality else null end,
		 'description', case when char_length(description) > 0 then description else null end)
	   ) as j
from intime.station;
select setval('intimev2.metadata_seq', (select max(id) from intimev2.metadata));

update intimev2.station
set meta_data_id = subj.mid
from (
	select s.id sid, m.id mid
	from intimev2.metadata m, intimev2.station s
	where m.station_id = s.id
) subj
where id = subj.sid;

-------------------------------------------------------------------------------------------------------------
-- bdpuser, bdprole, bdpusers_bdproles, bdprules
-------------------------------------------------------------------------------------------------------------
-- We need to delete default values first, since they are already present inside the old database, but maybe
-- with a different primary key.
delete from intimev2.bdpusers_bdproles;
delete from intimev2.bdpuser;
delete from intimev2.bdprules;
delete from intimev2.bdprole;

insert into intimev2.bdpuser table intime.bdpuser;
insert into intimev2.bdprole table intime.bdprole;
insert into intimev2.bdpusers_bdproles table intime.bdpusers_bdproles;
insert into intimev2.bdprules table intime.bdprules;


-------------------------------------------------------------------------------------------------------------
-- bicyclebasicdata, bikesharingstationbasicdata
-------------------------------------------------------------------------------------------------------------
-- Insert bike-sharing-stations as parent of bikes
update intimev2.station
set parent_id = subs.station
from (
	select station_id bike, bikesharingstation_id station
	from intime.bicyclebasicdata
) subs
where id = subs.bike;

-- Insert max availability of each type as json metadata object with keys from cnames
-- and values from max_available fields
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select station_id sid, jsonb_build_object('bikes', jsonb_object_agg(cname, max_available::int)) j
	from intime.bikesharingstationbasicdata b
	join type t on b.type_id = t.id
	group by sid
) subs
where station_id = subs.sid;

update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select id
			, jsonb_strip_nulls(jsonb_build_object(
				'type', regexp_replace(name, '\([0-9]*\)', '')
			)) j
	from intimev2.station
	where stationtype = 'Bicycle'
) subs
where subs.id = intimev2.metadata.station_id;


-------------------------------------------------------------------------------------------------------------
-- carpooling*basicdata & translations
-------------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select s.id station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'i18n', jsonb_build_object(
					'it', jsonb_build_object(
						'city', city,
						'address', address,
						'name', t.name
					)
				)
			)) j
	from intimev2.station s
	join intime.carpoolinghubbasicdata c on s.id = c.station_id
	join intime.carpoolinghubbasicdata_translation ct on ct.carpoolinghubbasicdata_id = c.id
	join intime.translation t on ct.i18n_id = t.id
) subs
where subs.station_id = intimev2.metadata.station_id;

-- Insert carpooling hubs as parent of carpooling users
update intimev2.station
set parent_id = subs.hub
from (
	select station_id usr, hub_id hub
	from intime.carpoolinguserbasicdata
) subs
where id = subs.usr;


-------------------------------------------------------------------------------------------------------------
-- carpoolinguserbasicdata & translations
-------------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select s.id station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'cartype', c.cartype,
				'gender', c.gender,
				'name', c.name,
				'pendular', c.pendular,
				'type', c.type,
				'hub', par.stationcode,
				'arrival', c.arrival,
				'departure', c.departure,
				'hubI18n', jsonb_build_object(
					'it', jsonb_build_object(
						'address', part.address,
						'city', part.city,
						'name', part.name
					)
				),
				'location', jsonb_build_object(
					'it', jsonb_build_object(
						'city', t.city,
						'address', t.address,
						'name', t.name
					)
				)
			)) j
	from intimev2.station s
	join intimev2.station par on s.parent_id = par.id
	join intime.carpoolinguserbasicdata c on s.id = c.station_id
	join intime.carpoolinguserbasicdata_translation ct on ct.carpoolinguserbasicdata_id = c.id
	join intime.translation t on ct.location_id = t.id
	join intime.carpoolinghubbasicdata parc on par.id = parc.station_id
	join intime.carpoolinghubbasicdata_translation parct on parct.carpoolinghubbasicdata_id = parc.id
	join intime.translation part on parct.i18n_id = part.id
) subs
where subs.station_id = intimev2.metadata.station_id;

-------------------------------------------------------------------------------------------------------------
-- carsharingcarstationbasicdata
-------------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'brand', brand,
				'licensePlate', licenseplate
			)) j
	from intime.carsharingcarstationbasicdata
) subs
where subs.station_id = intimev2.metadata.station_id;

-- Insert carsharing-stations as parent of carsharing-cars
update intimev2.station
set parent_id = subs.station
from (
	select station_id car, carsharingstation_id station
	from intime.carsharingcarstationbasicdata
) subs
where id = subs.car;


--------------------------------------------------------------------------------------------------------
-- carsharingstationbasicdata
--------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'bookahead', canbookahead,
				'company', companyshortname,
				'fixedParking', hasfixedparking,
				'availableVehicles', parking,
				'spontaneously', spontaneously
			)) j
	from intime.carsharingstationbasicdata
) subs
where subs.station_id = intimev2.metadata.station_id;

--------------------------------------------------------------------------------------------------------
-- carsharingstationbasicdata
--------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'brand', brand,
				'licensePlate', licenseplate
			)) j
	from intime.carsharingcarstationbasicdata
) subs
where subs.station_id = intimev2.metadata.station_id;


--------------------------------------------------------------------------------------------------------
-- classification, copert_emisfact, copert_parcom
--------------------------------------------------------------------------------------------------------

CREATE TABLE intimev2.classification (
	id serial NOT NULL,
	type_id int4 NULL,
	threshold varchar(512) NULL,
	min float8 NULL,
	max float8 NULL,
	CONSTRAINT classification_pkey PRIMARY KEY (id),
	CONSTRAINT classification_type_id_fkey FOREIGN KEY (type_id) REFERENCES intimev2.type(id) ON DELETE CASCADE
);

CREATE TABLE intimev2.copert_parcom (
	descriz bpchar(80) NOT NULL,
	id int4 NOT NULL,
	percent float4 NULL,
	id_class int2 NULL,
	eurocl int2 NULL,
	CONSTRAINT copert_parcom_id PRIMARY KEY (id)
);

CREATE TABLE intimev2.copert_emisfact (
	type_id int8 NOT NULL,
	copert_parcom_id int4 NOT NULL,
	v_min numeric(5,1) NOT NULL DEFAULT '-99.0'::numeric,
	v_max numeric(5,1) NOT NULL DEFAULT '-99.0'::numeric,
	coef_a float4 NULL,
	coef_b float4 NULL,
	coef_c float4 NULL,
	coef_d float4 NULL,
	coef_e float4 NULL,
	id serial NOT NULL,
	CONSTRAINT copert_emisfact_pk PRIMARY KEY (id),
	CONSTRAINT copert_emisfact_fk_type FOREIGN KEY (type_id) REFERENCES intimev2.type(id),
	CONSTRAINT copert_emisfact_id FOREIGN KEY (copert_parcom_id) REFERENCES intimev2.copert_parcom(id)
);

insert into intimev2.classification table intime.classification;
insert into intimev2.copert_parcom table intime.copert_parcom;
insert into intimev2.copert_emisfact table intime.copert_emisfact;


--------------------------------------------------------------------------------------------------------
-- echargingplugbasicdata
--------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select station_id
			, jsonb_strip_nulls(
				jsonb_build_object(
					'maxCurrent', maxcurrent::numeric,
					'maxPower', maxpower::numeric,
					'minCurrent', mincurrent::numeric,
					'outletTypeCode', plugtype
				)
			) j
	from intime.echargingplugbasicdata
) subs
where subs.station_id = intimev2.metadata.station_id;

-- Insert echarging-stations as parent of echarging-plugs
update intimev2.station
set parent_id = subs.station
from (
	select estation_id station, station_id plug
	from intime.echargingplugbasicdata
) subs
where id = subs.plug;


--------------------------------------------------------------------------------------------------------
-- echargingplugoutlet
--------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select s.id station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'outlets', array_agg(
					jsonb_build_object(
						'maxCurrent', maxcurrent::numeric,
						'maxPower', maxpower::numeric,
						'minCurrent', mincurrent::numeric,
						'outletTypeCode', plugtype,
						'hasFixedCable', hasfixedcable,
						'id', code
					)
				)
			)) j
	from intimev2.station s
	join intime.echargingplugoutlet o on o.plug_id = s.id
	join intimev2.metadata m on m.station_id = s.id
	group by s.id
) subs
where subs.station_id = intimev2.metadata.station_id;


--------------------------------------------------------------------------------------------------------
-- echargingstationbasicdata
--------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'provider', assetprovider,
				'capacity', chargingpointscount,
				'city', city,
				'state', state,
				'accessinfo', accessinfo,
				'address', address,
				'flashinfo', flashinfo,
				'locationserviceinfo', locationserviceinfo,
				'paymentInfo', paymentinfo,
				'reservable', reservable,
				'accessType', accesstype,
				'categories', categories
			)) j
	from intime.echargingstationbasicdata
) subs
where subs.station_id = intimev2.metadata.station_id;


update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'provider', assetprovider,
				'capacity', chargingpointscount,
				'city', city,
				'state', state,
				'accessInfo', accessinfo,
				'address', address,
				'flashinfo', flashinfo,
				'locationserviceinfo', locationserviceinfo,
				'paymentInfo', paymentinfo,
				'reservable', reservable,
				'accessType', accesstype,
				'categories', case
					when categories is null or categories = ''
						then null
						else string_to_array(categories, ',')
					end
			)) j
	from intime.echargingstationbasicdata
) subs
where subs.station_id = intimev2.metadata.station_id;


-------------------------------------------------------------------------------------------------------------
-- carparkingbasicdata, carparkingdynamic, carparkingdynamichistory
-------------------------------------------------------------------------------------------------------------
-- It is important to handle dynamic tables after any regular measurement/-history inserts

update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	with jsonobj as (
		select station_id
			   , jsonb_strip_nulls(jsonb_build_object(
				 'disabledcapacity', disabledcapacity,
				 'disabledtoiletavailable', disabledtoiletavailable,
				 'owneroperator', case when char_length(owneroperator) > 0 then owneroperator else null end,
				 'parkingtype', case when char_length(parkingtype) > 0 then parkingtype else null end,
				 'permittedvehicletypes', case when char_length(permittedvehicletypes) > 0 then permittedvehicletypes else null end,
				 'toiletsavailable', toiletsavailable,
				 'capacity', capacity,
				 'area', case when area is null then null else jsonb_build_object('srid', 4326, 'geotype', 'MULTIPOLYGON', 'dims', 2, 'area', area) end,
				 'phonenumber', case when char_length(phonenumber) > 0 then phonenumber else null end,
				 'email', case when char_length(email) > 0 then email else null end,
				 'url', case when char_length(url) > 0 then url else null end,
				 'mainaddress', case when char_length(mainaddress) > 0 then mainaddress else null end,
				 'station', station,
				 'state', state,
				 'womancapacity', womencapacity
				)) j
		from intime.carparkingbasicdata
	)
	select station_id, case when j::text = '{}'::text then null else j end
	from jsonobj
) subs
where subs.station_id = intimev2.metadata.station_id;

insert into intimev2.type (cname, cunit, description, rtype, created_on)
values ('occupied', null, 'Occupacy of a parking area', 'Count', now());

insert into intimev2.measurement (station_id, type_id, created_on, timestamp, double_value, period)
select station_id
		, (select id from intimev2.type where cname = 'occupied')
		, case when createdate is null then lastupdate else createdate end
		, lastupdate
		, occupacy
		, 300  			-- set 5 minutes = 300 seconds, since the lastupdate column shows more-or-less those differences
from intime.carparkingdynamic
where occupacy >= 0;

insert into intimev2.measurementhistory (station_id, type_id, created_on, timestamp, double_value, period)
select station_id
		, (select id from intimev2.type where cname = 'occupied')
		, case when createdate is null then lastupdate else createdate end
		, lastupdate
		, occupacy
		, 300  			-- set 5 minutes = 300 seconds, since the lastupdate column shows more-or-less those differences
from intime.carparkingdynamichistory
where occupacy >= 0;


--------------------------------------------------------------------------------------------------------
-- meteostationbasicdata
--------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'area', area
			)) j
	from intime.meteostationbasicdata
) subs
where subs.station_id = intimev2.metadata.station_id;

--------------------------------------------------------------------------------------------------------
-- linkbasicdata
--------------------------------------------------------------------------------------------------------
insert into intimev2.edge (id, edgedata_id, origin_id, destination_id, linegeometry, directed)
select id, station_id, origin_id, destination_id, linegeometry, true from intime.linkbasicdata;
select setval('intimev2.edge_seq', (select max(id) from intimev2.edge));

update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	with lbd as (
		select l.station_id id
			, jsonb_agg(jsonb_build_object(
				'lat', ST_X(ST_Transform(points.geom, 4326)),
				'lon', ST_Y(ST_Transform(points.geom, 4326))
			)) coords
		from intime.linkbasicdata l, st_dumppoints(l.linegeometry) as points(path, geom)
		group by 1
	)
	select station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'length', length,
				'street_ids_ref', street_ids_ref,
				'elapsed_time_default', elapsed_time_default,
				'destination', dest.stationcode,
				'coordinates', coords
			)) j
	from intime.linkbasicdata l
	join intimev2.station dest on l.destination_id = dest.id
	join lbd on lbd.id = l.station_id
) subs
where subs.station_id = intimev2.metadata.station_id;

--------------------------------------------------------------------------------------------------------
-- streetbasicdata
--------------------------------------------------------------------------------------------------------
update intimev2.metadata
set json = coalesce(json || subs.j, subs.j)
from (
	select station_id
			, jsonb_strip_nulls(jsonb_build_object(
				'length', length,
				'description', description,
				'old_idstr', old_idstr,
				'speed_default', speed_default,
				'linegeometry', linegeometry
			)) j
	from intime.streetbasicdata
) subs
where subs.station_id = intimev2.metadata.station_id;


--------------------------------------------------------------------------------------------------------
-- trafficstreetfactor
--------------------------------------------------------------------------------------------------------
-- Select both stations for spira and arco and combine it into a single new
-- TrafficStreetFactor station, which represents the edge information
-- In addition, add a sequential number to the selection and insert statements
-- to be able to join them later. See https://stackoverflow.com/a/29263402 for
-- further details...
with
sel as (
	select row_number() over () as rn
			, s.name || ' + ' || s2.name as name
			, s.stationcode || ' + ' || s2.stationcode as stationcode
			, 'TrafficStreetFactor' as stationtype
			, id_arco
			, id_spira
	from intime.trafficstreetfactor
	join station s on id_arco = s.id
	join station s2 on id_spira = s2.id
	order by rn
)
, ins1 as (
	insert into intimev2.station (active, available, name, stationcode, stationtype)
	select true, true, name, stationcode, stationtype
	from sel
	returning id as station_id
)
, ins2 as (
	insert into intimev2.edge (edgedata_id, origin_id, destination_id)
	select station_id, id_arco, id_spira
	from (select *, row_number() over () as rn from ins1) i
	join sel s using (rn)
	returning *
)
select * from ins2;

insert into intimev2.metadata (created_on, station_id, json)
select now()
		, e.edgedata_id
		, jsonb_build_object(
			'factor', factor,
			'length', length,
			'hv_perc', hv_perc
		)
from intime.trafficstreetfactor t
join edge e on e.origin_id = t.id_arco and e.destination_id = t.id_spira;


--------------------------------------------------------------------------------------------------------
-- Final cleansing of station and metadata
--------------------------------------------------------------------------------------------------------

-- Update station-to-metadata foreign keys
-- NB: this must be the last thing to do, because we update metadata foreign keys multiple times
update intimev2.station
set meta_data_id = subj.mid
from (
	select s.id sid, m.id mid
	from intimev2.metadata m, intimev2.station s
	where m.station_id = s.id
) subj
where id = subj.sid and meta_data_id is null;

-- Remove all empty JSON objects from metadata
update intimev2.metadata
set json = null
where json = '{}'::jsonb;

--------------------------------------------------------------------------------------------------------
-- measurement(string|mobile), measurement(string|mobile)history, elaboration & elaborationhistory
--------------------------------------------------------------------------------------------------------
insert into intimev2.provenance (id, lineage, data_collector) values (1, 'NOI', 'Migration from V1: Elaborations');
insert into intimev2.provenance (id, lineage, data_collector) values (2, 'VARIOUS', 'Migration from V1: Measurements');

insert into intimev2.measurement (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, period, 2 /* provenance ID, see above */
from intime.measurement where value is not null;

insert into intimev2.measurementhistory (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, period, 2 /* provenance ID, see above */
from intime.measurementhistory where id >= 0 and id < 100000000 and value is not null;
insert into intimev2.measurementhistory (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, period, 2 /* provenance ID, see above */
from intime.measurementhistory where id >= 100000000 and id < 200000000 and value is not null;
insert into intimev2.measurementhistory (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, period, 2 /* provenance ID, see above */
from intime.measurementhistory where id >= 200000000 and id < 300000000 and value is not null;
insert into intimev2.measurementhistory (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, period, 2 /* provenance ID, see above */
from intime.measurementhistory where id >= 300000000 and id < 400000000 and value is not null;
insert into intimev2.measurementhistory (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, period, 2 /* provenance ID, see above */
from intime.measurementhistory where id >= 400000000 and value is not null;

insert into intimev2.measurementstring (created_on, timestamp, string_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, 1 /* instant, will be removed in the near future */, 2 /* provenance ID, see above */
from intime.measurementstring where value is not null;

insert into intimev2.measurementstringhistory (created_on, timestamp, string_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, 1 /* instant, will be removed in the near future */, 2 /* provenance ID, see above */
from intime.measurementstringhistory where value is not null;

create table intimev2.measurementmobile as select * from intime.measurementmobile;

create table intimev2.measurementmobilehistory as select * from intime.measurementmobilehistory;

CREATE INDEX idx_measurementmobilehistory_station_id ON intimev2.measurementmobilehistory USING btree (station_id);
CREATE INDEX idx_measurementmobilehistory_no2_1_microgm3_ma ON intimev2.measurementmobilehistory USING btree (no2_1_microgm3_ma);
CREATE INDEX idx_measurementmobilehistory_no2_1_ppb ON intimev2.measurementmobilehistory USING btree (no2_1_ppb);
CREATE INDEX idx_measurementmobilehistory_ts_ms ON intimev2.measurementmobilehistory USING btree (ts_ms);


insert into intimev2.measurementhistory (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select
	case when created_on is null then timestamp else created_on end
	, timestamp
	, value
	, station_id
	, type_id
	, period
	, 1 /* provenance ID, see above */
from intime.elaborationhistory where value is not null;

insert into intimev2.measurement (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select
	case when created_on is null then timestamp else created_on end
	, timestamp
	, value
	, station_id
	, type_id
	, period
	, 1 /* provenance ID, see above */
from intime.elaboration where value is not null;

DELETE FROM intimev2.measurementstring a USING intimev2.measurementstring b
WHERE
    a.id < b.id
    AND a.station_id = b.station_id AND a.type_id = b.type_id AND a.period = b.period;

DELETE FROM intimev2.measurementstringhistory a USING intimev2.measurementstringhistory b
WHERE
    a.id < b.id
    AND a.station_id = b.station_id AND a.type_id = b.type_id AND a.period = b.period AND a.timestamp = b.timestamp AND a.value = b.value;

DELETE FROM intimev2.measurement a USING intimev2.measurement b
WHERE
    a.id < b.id
    AND a.station_id = b.station_id AND a.type_id = b.type_id AND a.period = b.period;

alter table measurement add constraint uc_measurement_station_id_type_id_period unique (station_id, type_id, period);

DELETE FROM intimev2.measurementhistory a USING intimev2.measurementhistory b
WHERE
    a.id < b.id
    AND a.station_id = b.station_id AND a.type_id = b.type_id AND a.period = b.period AND a.timestamp = b.timestamp AND a.value = b.value;

   
alter table measurement 
add constraint uc_measurement_station_id_type_id_period 
unique (station_id, type_id, period);
   
alter table measurementhistory 
add constraint uc_measurementhistory_station_i__timestamp_period_double_value_ 
unique (station_id, type_id, timestamp, period, double_value);   

alter table measurementstring 
add constraint uc_measurementstring_station_id_type_id_period 
unique (station_id, type_id, period);

alter table measurementstringhistory 
add constraint uc_measurementstringhistory_sta__timestamp_period_string_value_ 
unique (station_id, type_id, timestamp, period, string_value);



