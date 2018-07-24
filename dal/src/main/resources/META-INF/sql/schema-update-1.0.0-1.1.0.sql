-- schema-update-1.0.0-1.1.0.sql
-- Change bdppermissions from type table to a materialized view
--
-- Changelog:
--   1.0.0 - Initial schema (dumped from production on 19-Jul-2018)
--   1.1.0 - Change bdppermissions from type table to a materialized view


alter table intime.bdppermissions rename to bdppermissions_oldtable;

create MATERIALIZED VIEW intime.bdppermissions as
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

comment on materialized view intime.bdppermissions is 'Materialized view to select measurements allowed to see by a certain role (=group).';

drop view intime.bdppermissions_old;

ALTER TABLE intime.bdppermissions OWNER to bdp;
GRANT SELECT ON TABLE intime.bdppermissions TO bdp_readonly;
GRANT ALL ON TABLE intime.bdppermissions TO bdp;

DROP INDEX IF EXISTS intime.bdppermissions_stp_idx;
DROP INDEX IF EXISTS intime.bdppermissions_rold_id_idx;
DROP INDEX IF EXISTS intime.bdppermissions_role_id_idx;
DROP INDEX IF EXISTS intime.bdppermissions_uuid_idx;
DROP INDEX IF EXISTS intime.bdppermissions_rstp_idx;

CREATE INDEX bdppermissions_role_id_idx ON intime.bdppermissions USING btree(role_id);
CREATE INDEX bdppermissions_stp_idx ON intime.bdppermissions USING btree(station_id, type_id, period);
CREATE UNIQUE INDEX bdppermissions_uuid_idx ON intime.bdppermissions USING btree(uuid);

ALTER TABLE intime.bdppermissions CLUSTER ON bdppermissions_stp_idx;

delete from intime.schemaversion;
insert into intime.schemaversion values ('1.1.0');

