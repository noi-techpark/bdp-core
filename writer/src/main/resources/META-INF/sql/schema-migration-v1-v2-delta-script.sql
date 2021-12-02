/*
 * This script takes the DELTA from the old schema and transfers it to the new one.
 * The boundary of the DELTA is the last measured record within the new schema.
 *
 * We insert only new data types and stations, that is, we do not update changed data of
 * existing types and stations.
 *
 * We then fetch the DELTAs from elaborationhistory, measurementhistory, measurementstringhistory,
 * and carparkingdynamichistory and insert them into measurementhistory and measurementstringhistory
 * respectively.
 *
 * We mark all new measurements with the provencance defined below.
 *
 * We print the last created_on value from measurementhistory and measurementstringhistory at the
 * beginning and the end, such that, we keep track of what has been done.
 *
 * Finally, we also compare station and data type tables with the old ones, and print differences.
 *
 * @author Peter Moser
 *
 */

-- When do we have inserted the last entries in measurement tables (before update)?
select max(created_on) from intimev2.measurementhistory
union all
select max(created_on) from intimev2.measurementstringhistory;

-- DELTA MIGRATION START ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
begin;

insert into intimev2.provenance (id, lineage, data_collector)
values (3, 'VARIOUS', 'Delta migration-script from V1 to V2')
on conflict do nothing;

insert into intimev2.station(active, available, name, origin, pointprojection, stationcode, stationtype, parent_id)
select active, available, name, origin, pointprojection, stationcode, stationtype, parent_id from intime.station
on conflict do nothing;

insert into intimev2.type(cname, created_on, cunit, description, rtype)
select cname, created_on, cunit, description, rtype from intime.type
on conflict do nothing;

-- measurement
delete from intimev2.measurement;
insert into intimev2.measurement (created_on, period, timestamp, double_value, station_id, type_id, provenance_id)
select created_on, period, timestamp, value, station_id, type_id, 3 from intime.measurement
where value is not null;

-- measurementstring
delete from intimev2.measurementstring;
insert into intimev2.measurementstring (created_on, period, timestamp, string_value, station_id, type_id, provenance_id)
select created_on, period, timestamp, value, station_id, type_id, 3 from intime.measurementstring
where value is not null;

-- carparkingdynamichistory
insert into intimev2.measurementhistory (station_id, type_id, created_on, timestamp, double_value, period, provenance_id)
select station_id
		, (select id from intimev2.type where cname = 'occupied')
		, case when createdate is null then lastupdate else createdate end
		, lastupdate
		, occupacy
		, 300  			-- set 5 minutes = 300 seconds, since the lastupdate column shows more-or-less those differences
		, 3
from intime.carparkingdynamichistory
where occupacy >= 0 and lastupdate > (select max(created_on) from intimev2.measurementhistory) --now() - interval '1 day'
on conflict do nothing;

-- measurementhistory
insert into intimev2.measurementhistory (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, period, 3 /* provenance ID, see above */
from intime.measurementhistory where created_on > (select max(created_on) from intimev2.measurementhistory) --now() - interval '1 day'
and value is not null
on conflict do nothing;

-- measurementstringhistory
insert into intimev2.measurementstringhistory (created_on, timestamp, string_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, period, 3 /* provenance ID, see above */
from intime.measurementstringhistory where created_on > (select max(created_on) from intimev2.measurementstringhistory) --now() - interval '1 day'
and value is not null
on conflict do nothing;

-- elaborationhistory
insert into intimev2.measurementhistory (created_on, timestamp, double_value, station_id, type_id, period, provenance_id)
select
	case when created_on is null then timestamp else created_on end
	, timestamp
	, value
	, station_id
	, type_id
	, period
	, 3 /* provenance ID, see above */
from intime.elaborationhistory where created_on > (select max(created_on) from intimev2.measurementhistory) --now() - interval '1 day' and value is not null
on conflict do nothing;

commit;
-- DELTA MIGRATION END -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- When do we have inserted the last entries in measurement tables (after update)?
select max(created_on) from intimev2.measurementhistory
union all
select max(created_on) from intimev2.measurementstringhistory;


-- See differences in type and station tables
-- We exclude TrafficStreetFactor, because it exists only inside V2
WITH test AS (
	select active, available, name, origin, stationcode, stationtype, parent_id from intime.station
	where stationtype <> 'TrafficStreetFactor'
)
, test2 AS (
	select active, available, name, origin, stationcode, stationtype, parent_id from intimev2.station
	where stationtype <> 'TrafficStreetFactor'
) SELECT * FROM ((TABLE test EXCEPT ALL TABLE test2) UNION (TABLE test2 EXCEPT ALL TABLE test)) d;

WITH test AS (
	select cname, created_on, cunit, description, rtype from intime.type
)
, test2 AS (
	select cname, created_on, cunit, description, rtype from intimev2.type
) SELECT * FROM ((TABLE test EXCEPT ALL TABLE test2) UNION (TABLE test2 EXCEPT ALL TABLE test)) d;