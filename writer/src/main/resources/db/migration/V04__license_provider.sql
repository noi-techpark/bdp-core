
set search_path to ${default_schema}, public;

alter table provenance
add license varchar(255);
alter table provenance
add provider varchar(255);
alter table provenance 
add source varchar(255);

create index idx_provenance_provider on provenance(provider);
create index idx_provenance_source on provenance(source);

alter table metadata 
add provenance_id int8;
alter table metadata
add constraint fk_metadata_provenance_id foreign key (provenance_id) references provenance (id);
