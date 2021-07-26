create sequence event_location_seq start 1 increment 1;
create sequence event_seq start 1 increment 1;
create table event (id int8 default nextval('event_seq') not null, category varchar(255), created_on timestamp, description text, event_interval bytea, uuid varchar(255), location_id int8, meta_data_id int8, primary key (id));
create table location (id int8 default nextval('event_location_seq') not null, description text, geometry GEOMETRY, primary key (id));
alter table event add constraint uc_event_uuid unique (uuid);
alter table event add constraint fk_event_location_id_location_pk foreign key (location_id) references location;
alter table event add constraint fk_event_meta_data_id_metadata_pk foreign key (meta_data_id) references metadata;
