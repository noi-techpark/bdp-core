-- schema-update-2.1.1-3.0.0.sql
-- Change bdppermissions from type table to a materialized view
--
set search_path=public,intimev2;
BEGIN;
alter table provenance add column uuid character varying(255);
update provenance set uuid = (substring(MD5(random()::text),1,8)) where uuid is null;
alter table provenance alter column uuid set not null; 
alter table provenance add constraint uc_provenance_uuid unique(uuid);
delete from schemaversion;
insert into schemaversion values ('3');
COMMIT;
