alter table type add column meta_data_id bigint;
create sequence intimev2.type_metadata_seq;
create table intimev2.type_metadata(id bigint not null default nextval('type_metadata_seq'), created_on timestamp without time zone, json jsonb, type_id bigint references type(id), primary key(id)); 
alter table type add CONSTRAINT fk_type_meta_data_id_type_metadata_pk foreign key (meta_data_id) references type_metadata(id);

