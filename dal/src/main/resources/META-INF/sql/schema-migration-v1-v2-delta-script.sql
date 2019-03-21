-- When do we have inserted the last entries in measurement tables (before update)?
select max(created_on) from intimev2.measurementhistory
union all
select max(created_on) from intimev2.measurementstringhistory;

insert into intimev2.provenance (id, lineage, data_collector) values 
    (3, 'NOI', 'Delta script from V1: carparkingdynamichistory'), 
    (4, 'NOI', 'Delta script from V1: measurementhistory'),
    (5, 'NOI', 'Delta script from V1: measurementstringhistory'),
    (6, 'NOI', 'Delta script from V1: elaborationhistory')
on conflict do nothing;


-- DELTA MIGRATION START ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
begin;

insert into intimev2.station(active, available, name, origin, pointprojection, stationcode, stationtype, parent_id)
select active, available, name, origin, pointprojection, stationcode, stationtype, parent_id from intime.station
on conflict do nothing;

insert into intimev2.type(cname, created_on, cunit, description, rtype)
select cname, created_on, cunit, description, rtype from intime.type
on conflict do nothing;

-- measurement
delete from intimev2.measurement;
insert into intimev2.measurement (created_on, period, timestamp, double_value, station_id, type_id, provenance_id)
select created_on, period, timestamp, value, station_id, type_id, 4 from intime.measurement;

-- measurementstring
delete from intimev2.measurementstring;
insert into intimev2.measurementstring (created_on, period, timestamp, double_value, station_id, type_id, provenance_id)
select created_on, period, timestamp, value, station_id, type_id, 5 from intime.measurementstring;

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
select created_on, timestamp, value, station_id, type_id, period, 4 /* provenance ID, see above */
from intime.measurementhistory where created_on > (select max(created_on) from intimev2.measurementhistory) --now() - interval '1 day' 
and value is not null
on conflict do nothing;

-- measurementstringhistory
insert into intimev2.measurementstringhistory (created_on, timestamp, string_value, station_id, type_id, period, provenance_id)
select created_on, timestamp, value, station_id, type_id, period, 5 /* provenance ID, see above */
from intime.measurementhistory where created_on > (select max(created_on) from intimev2.measurementstringhistory) --now() - interval '1 day' 
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
	, 6 /* provenance ID, see above */
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