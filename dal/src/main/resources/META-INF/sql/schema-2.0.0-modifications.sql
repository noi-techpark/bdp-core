------------------------------------------------------------------------------------------------------------------------
-- Schema versioning
--     We want to know which schema is currently installed
--     Documentation: https://opendatahub.readthedocs.io/en/latest/guidelines/database.html
------------------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS schemaversion (
    version character varying COLLATE pg_catalog."default" NOT NULL
);
COMMENT ON TABLE schemaversion
    IS 'Version of the current schema (used for scripted updates)';
DELETE FROM schemaversion;
INSERT INTO schemaversion VALUES ('2.0.0');

------------------------------------------------------------------------------------------------------------------------
-- Permission handling
------------------------------------------------------------------------------------------------------------------------

CREATE VIEW bdproles_unrolled AS
 WITH RECURSIVE roles(role, subroles) AS (
         SELECT bdprole.id,
            ARRAY[bdprole.id] AS "array"
           FROM bdprole
          WHERE (bdprole.parent_id IS NULL)
        UNION ALL
         SELECT t.id,
            (roles_1.subroles || t.id)
           FROM bdprole t,
            roles roles_1
          WHERE (t.parent_id = roles_1.role)
        )
 SELECT roles.role,
    unnest(roles.subroles) AS sr
   FROM roles;

CREATE VIEW bdpfilters_unrolled AS
 SELECT DISTINCT x.role,
    f.station_id,
    f.type_id,
    f.period
   FROM (bdprules f
     JOIN bdproles_unrolled x ON ((f.role_id = x.sr)))
  ORDER BY x.role;


drop table bdppermissions cascade;
create materialized view bdppermissions as
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

CREATE INDEX bdppermissions_stp_idx ON bdppermissions USING btree (station_id, type_id, period);
CREATE INDEX bdppermissions_role_id_idx ON bdppermissions USING btree (role_id);
CREATE UNIQUE INDEX bdppermissions_uuid_idx ON bdppermissions USING btree (uuid);
COMMENT ON MATERIALIZED VIEW bdppermissions IS 'Materialized view to simulate row-level-security';



------------------------------------------------------------------------------------------------------------------------
-- Initial data
------------------------------------------------------------------------------------------------------------------------

-- Add GUEST (sees nothing, used to define open-data), and ADMIN (sees everything) role.
-- If you change something here, please make sure that the documentation is also updated.
-- See https://github.com/idm-suedtirol/documentation/wiki/ODH-Permission-handling
INSERT INTO bdprole(name, description) VALUES ('GUEST', 'Default role, that sees open data');
INSERT INTO bdprole(name, description) VALUES ('ADMIN', 'Default role, that sees all data');
INSERT INTO bdprules(role_id, station_id, type_id, period)
    VALUES ((SELECT id FROM bdprole WHERE name = 'ADMIN'), null, null, null);

-- create default admin user and association to admin role
insert into bdpuser(email, enabled, password, token_expired)
	values('admin', true, crypt('123456789', gen_salt('bf')), false);
insert into bdpusers_bdproles(user_id,role_id)
    select u.id, r.id from bdpuser u
    cross join bdprole r
    where u.email = 'admin' and r.name ='ADMIN';

REFRESH MATERIALIZED VIEW bdppermissions;

------------------------------------------------------------------------------------------------------------------------
-- Additional indexes
------------------------------------------------------------------------------------------------------------------------
CREATE INDEX measurementhistory_tsdesc_idx ON measurementhistory USING btree ("timestamp" DESC);
CREATE INDEX measurementstringhistory_tsdesc_idx ON measurementstringhistory USING btree ("timestamp" DESC);
