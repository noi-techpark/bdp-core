-------------------------------------------------------------------------------------------------------------
-- 								MIGRATION SCRIPT FROM v5.2.0 TO v5.3.0									   --
-------------------------------------------------------------------------------------------------------------
--
-- Run this script as follows:
-- PGPASSWORD=secr3t psql -v ON_ERROR_STOP=1 --echo-all -U <user> -p 5432 -h <host> -d <db> -f this-script.sql
--
-- To avoid data loss, always use ON_ERROR_STOP!
--
-- Author: p.moser@noi.bz.it
--
-------------------------------------------------------------------------------------------------------------

-- Make sure this script does not touch wrong schemas
DO language plpgsql $$
DECLARE
	count INT;
BEGIN
	SELECT COUNT(*) INTO count FROM intimev2.schemaversion WHERE version = '5.2.0';
	IF count <> 1 THEN
		RAISE EXCEPTION 'Wrong schema version... terminating script';
	END IF;
END
$$;


create sequence intimev2.event_location_seq start 1 increment 1;
create sequence intimev2.event_seq start 1 increment 1;
create table intimev2.event (id int8 default nextval('event_seq') not null, category varchar(255), created_on timestamp, description text, event_interval bytea, uuid varchar(255), location_id int8, meta_data_id int8, primary key (id));
create table intimev2.location (id int8 default nextval('event_location_seq') not null, description text, geometry public.GEOMETRY, primary key (id));
alter table intimev2.event add constraint uc_event_uuid unique (uuid);
alter table intimev2.event add constraint fk_event_location_id_location_pk foreign key (location_id) references intimev2.location;
alter table intimev2.event add constraint fk_event_meta_data_id_metadata_pk foreign key (meta_data_id) references intimev2.metadata;

GRANT ALL ON TABLE intimev2.event TO bdp;
GRANT ALL ON TABLE intimev2.location TO bdp;

GRANT SELECT ON TABLE intimev2."event" TO bdp_manager;
GRANT INSERT ON TABLE intimev2."event" TO bdp_manager;
GRANT UPDATE ON TABLE intimev2."event" TO bdp_manager;
GRANT SELECT ON TABLE intimev2."location" TO bdp_manager;
GRANT INSERT ON TABLE intimev2."location" TO bdp_manager;
GRANT UPDATE ON TABLE intimev2."location" TO bdp_manager;

GRANT SELECT ON TABLE intimev2."event" TO bdp_readonly;
GRANT SELECT ON TABLE intimev2."location" TO bdp_readonly;

delete from schemaversion;
insert into schemaversion values('5.3.0');

