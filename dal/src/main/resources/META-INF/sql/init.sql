--
-- Permission handling
--
drop view if exists bdproles_unrolled;
create view bdproles_unrolled as
    with recursive roles(role, subroles) as (
        select id, ARRAY[id]::bigint[]
        	from bdprole
        	where parent_id is null
        union all
        select t.id, roles.subroles || t.id
        	from bdprole t, roles
        	where t.parent_id = roles.role
    ) select role, unnest(subroles) as sr
    	from roles;

drop view if exists bdpfilters_unrolled;
create view bdpfilters_unrolled as
    select distinct x.role, station_id, type_id, period
    	from bdprules f
    	join bdproles_unrolled x on f.role_id = x.sr
    	order by x.role;

drop table if exists bdppermissions;
drop view if exists bdppermissions;
create view bdppermissions as
with x as (
    select row_number() over (order by role asc) as uuid
	    , role as role_id
        , station_id
        , type_id
        , period
        , bool_or(station_id is null) over (partition by role) as e_stationid
        , bool_or(type_id is null) over (partition by role, station_id) as e_typeid
        , bool_or(period is null) over (partition by role, station_id, type_id) as e_period
    	from bdpfilters_unrolled
    	order by role, station_id, type_id, period
) select uuid, role_id, station_id, type_id, period
	from x
	where (x.station_id is null and x.type_id is null and x.period is null) or
    	  (x.station_id is not null and x.type_id is null and x.period is null and not e_stationid) or
    	  (x.station_id is not null and x.type_id is not null and x.period is null and not e_stationid and not e_typeid) or
    	  (x.station_id is not null and x.type_id is not null and x.period is not null and not e_stationid and not e_typeid and not e_period);

