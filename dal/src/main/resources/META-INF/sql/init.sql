------------------------------------------------------------------------------------------------------------------------
-- Permission handling
------------------------------------------------------------------------------------------------------------------------
drop view if exists intime.bdproles_unrolled cascade;
create view intime.bdproles_unrolled as
    with recursive roles(role, subroles) as (
        select id, ARRAY[id]::bigint[]
            from intime.bdprole
            where parent_id is null
        union all
        select t.id, roles.subroles || t.id
            from intime.bdprole t, roles
            where t.parent_id = roles.role
    ) select role, unnest(subroles) as sr
        from roles;


drop view if exists intime.bdpfilters_unrolled cascade;
create view intime.bdpfilters_unrolled as
    select distinct x.role, station_id, type_id, period
        from intime.bdprules f
        join intime.bdproles_unrolled x on f.role_id = x.sr
        order by x.role;

drop table if exists intime.bdppermissions;
drop view if exists intime.bdppermissions cascade;
create view intime.bdppermissions as
with x as (
    select row_number() over (order by role asc) as uuid
        , role as role_id
        , station_id
        , type_id
        , period
        , bool_or(station_id is null) over (partition by role) as e_stationid
        , bool_or(type_id is null) over (partition by role, station_id) as e_typeid
        , bool_or(period is null) over (partition by role, station_id, type_id) as e_period
        from intime.bdpfilters_unrolled
        order by role, station_id, type_id, period
) select uuid, role_id, station_id, type_id, period
    from x
    where (x.station_id is null and x.type_id is null and x.period is null) or
          (x.station_id is not null and x.type_id is null and x.period is null and not e_stationid) or
          (x.station_id is not null and x.type_id is not null and x.period is null and not e_stationid and not e_typeid) or
          (x.station_id is not null and x.type_id is not null and x.period is not null and not e_stationid and not e_typeid and not e_period);


------------------------------------------------------------------------------------------------------------------------
-- Initial data
------------------------------------------------------------------------------------------------------------------------

-- Add GUEST (sees nothing, used to define open-data), and ADMIN (sees everything) role.
-- If you change something here, please make sure that the documentation is also updated.
-- See https://github.com/idm-suedtirol/documentation/wiki/ODH-Permission-handling
INSERT INTO intime.bdprole(name, description) VALUES ('GUEST', 'Default role, that sees open data')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO intime.bdprole(name, description) VALUES ('ADMIN', 'Default role, that sees all data')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;
INSERT INTO intime.bdprules(role_id, station_id, type_id, period)
    VALUES ((SELECT id FROM bdprole WHERE name = 'ADMIN'), null, null, null)
    ON CONFLICT DO NOTHING;


------------------------------------------------------------------------------------------------------------------------
-- Fix sequences
------------------------------------------------------------------------------------------------------------------------

-- Update sequences to be at the top of any serial id, because Hibernate does not
-- do that automatically at startup. It just queries the max value of each sequence
-- to get the next value. XXX Currently only a few hard-coded values here, this list
-- should probably be generated and updated with a script.
select setval('intime.station_seq', (select max(id) from intime.station));
select setval('intime.type_seq', (select max(id) from intime.type));
select setval('intime.measurement_id_seq', (select max(id) from intime.measurement));

-- create default admin user and association to admin role
insert into bdpuser(email,enabled,password,tokenexpired) values('admin',true,'Z.MezJ8Y8HVtsgNMrFBKCOAz3CCaVkKawHBIhUj.wSdElYHb/KcZS',false);
insert into bdpusers_bdproles(user_id,role_id) select u.id,r.id from bdpuser u cross join bdprole r  where u.email='admin' and r.name ='ADMIN';
