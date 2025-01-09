
set search_path to ${default_schema}, public;

alter table provenance add license varchar(255);
alter table provenance add source varchar(255);
alter table provenance add "owner" varchar(255);
alter table provenance alter column lineage drop not null;
alter table provenance drop constraint uc_provenance_lineage_data_collector_data_collector_version;
alter table provenance add constraint uc_provenance_data_collector_license_source_owner unique ( data_collector, data_collector_version, license, source, owner);

alter table metadata add provenance_id int8;
alter table metadata add constraint fk_metadata_provenance foreign key (provenance_id) references provenance(id);

alter table provenance alter column lineage drop not null;

