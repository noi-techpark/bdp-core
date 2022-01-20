/*
 * If you use migration scripts within Java Application, then you need a "mvn clean",
 * because otherwise the copy inside the "target" folder gets used, which might be outdated
 */
set search_path to ${flyway:defaultSchema}, public;
create extension if not exists postgis;

create sequence metadata_seq start 1 increment 1;
create table metadata (
	id int8 default nextval('metadata_seq') not null,
	created_on timestamp,
	json jsonb,
	station_id int8,
	primary key (id)
);

create sequence provenance_seq start 1 increment 1;
create table provenance (
	id int8 default nextval('provenance_seq') not null,
	data_collector varchar(255) not null,
	data_collector_version varchar(255),
	lineage varchar(255) not null,
	uuid varchar(255) not null,
	primary key (id),
	constraint uc_provenance_lineage_data_collector_data_collector_version unique (lineage, data_collector, data_collector_version),
	constraint uc_provenance_uuid unique (uuid)
);

create sequence station_seq start 1 increment 1;
create table station (
	id int8 default nextval('station_seq') not null,
	active boolean,
	available boolean,
	name varchar(255) not null,
	origin varchar(255),
	pointprojection geometry,
	stationcode varchar(255) not null,
	stationtype varchar(255) not null,
	meta_data_id int8,
	parent_id int8,
	primary key (id),
	constraint uc_station_stationcode_stationtype unique (stationcode, stationtype)
);

create sequence type_seq start 1 increment 1;
create table type (
	id int8 default nextval('type_seq') not null,
	cname varchar(255) not null,
	created_on timestamp,
	cunit varchar(255),
	description varchar(255),
	rtype varchar(255),
	meta_data_id int8,
	primary key (id),
	constraint uc_type_cname unique (cname)
);

create sequence type_metadata_seq start 1 increment 1;
create table type_metadata (
	id int8 default nextval('type_metadata_seq') not null,
	created_on timestamp,
	json jsonb,
	type_id int8,
	primary key (id)
);


create sequence edge_seq start 1 increment 1;
create table edge (
	id int8 default nextval('edge_seq') not null,
	directed boolean default true not null,
	linegeometry geometry,
	destination_id int8,
	edge_data_id int8,
	origin_id int8,
	primary key (id)
);

create sequence event_seq start 1 increment 1;
create table "event" (
	id int8 default nextval('event_seq') not null,
	category varchar(255),
	created_on timestamp,
	description text,
	event_interval tsrange,
	origin varchar(255),
	uuid varchar(255) not null,
	location_id int8,
	meta_data_id int8,
	provenance_id int8 not null,
	primary key (id),
	constraint uc_event_uuid unique (uuid)
);

create sequence event_location_seq start 1 increment 1;
create table location (
	id int8 default nextval('event_location_seq') not null,
	description text,
	geometry geometry not null,
	primary key (id)
);

create sequence measurement_seq start 1 increment 1;
create table measurement (
	id int8 default nextval('measurement_seq') not null,
	created_on timestamp not null,
	period int4 not null,
	timestamp timestamp not null,
	double_value float8 not null,
	provenance_id int8,
	station_id int8 not null,
	type_id int8 not null,
	primary key (id),
	constraint uc_measurement_station_id_type_id_period unique (station_id, type_id, period)
);

create sequence measurementhistory_seq start 1 increment 1;
create table measurementhistory (
	id int8 default nextval('measurementhistory_seq') not null,
	created_on timestamp not null,
	period int4 not null,
	timestamp timestamp not null,
	double_value float8 not null,
	provenance_id int8,
	station_id int8 not null,
	type_id int8 not null,
	primary key (id),
	constraint uc_measurementhistory_station_i__timestamp_period_double_value_ unique (station_id, type_id, timestamp, period, double_value)
);

create sequence measurement_json_seq start 1 increment 1;
create table measurementjson (
	id int8 default nextval('measurement_json_seq') not null,
	created_on timestamp not null,
	period int4 not null,
	timestamp timestamp not null,
	json_value jsonb,
	provenance_id int8,
	station_id int8 not null,
	type_id int8 not null,
	primary key (id),
	constraint uc_measurementjson_station_id_type_id_period unique (station_id, type_id, period)
);

create sequence measurementhistory_json_seq start 1 increment 1;
create table measurementjsonhistory (
	id int8 default nextval('measurementhistory_json_seq') not null,
	created_on timestamp not null,
	period int4 not null,
	timestamp timestamp not null,
	json_value jsonb,
	json_value_md5 varchar(32) generated always as (md5(json_value::text)) stored,
	provenance_id int8,
	station_id int8 not null,
	type_id int8 not null,
	primary key (id),

	/* The md5 checksum is not possible within a "constraint" syntax
	 * This is to prevent the following error:
	 *     Values larger than 1/3 of a buffer page cannot be indexed.
	 *     Consider a function index of an MD5 hash of the value, or use full text indexing.
	 */
	constraint uc_measurementjsonhistory_stati__time__p_period_json_value_md5_ unique (station_id, type_id, "timestamp", period, json_value_md5)
);

create sequence measurementstring_seq start 1 increment 1;
create table measurementstring (
	id int8 default nextval('measurementstring_seq') not null,
	created_on timestamp not null,
	period int4 not null,
	timestamp timestamp not null,
	string_value varchar(255) not null,
	provenance_id int8,
	station_id int8 not null,
	type_id int8 not null,
	primary key (id),
	constraint uc_measurementstring_station_id_type_id_period unique (station_id, type_id, period)
);

create sequence measurementstringhistory_seq start 1 increment 1;
create table measurementstringhistory (
	id int8 default nextval('measurementstringhistory_seq') not null,
	created_on timestamp not null,
	period int4 not null,
	timestamp timestamp not null,
	string_value varchar(255) not null,
	provenance_id int8,
	station_id int8 not null,
	type_id int8 not null,
	primary key (id),
	constraint uc_measurementstringhistory_sta__timestamp_period_string_value_ unique (station_id, type_id, timestamp, period, string_value)
);

alter table edge add constraint fk_edge_destination_id_station_pk foreign key (destination_id) references station;
alter table edge add constraint fk_edge_edge_data_id_station_pk foreign key (edge_data_id) references station;
alter table edge add constraint fk_edge_origin_id_station_pk foreign key (origin_id) references station;
alter table event add constraint fk_event_location_id_location_pk foreign key (location_id) references location;
alter table event add constraint fk_event_meta_data_id_metadata_pk foreign key (meta_data_id) references metadata;
alter table event add constraint fk_event_provenance_id_provenance_pk foreign key (provenance_id) references provenance;
alter table measurement add constraint fk_measurement_provenance_id_provenance_pk foreign key (provenance_id) references provenance;
alter table measurement add constraint fk_measurement_station_id_station_pk foreign key (station_id) references station;
alter table measurement add constraint fk_measurement_type_id_type_pk foreign key (type_id) references type;
alter table measurementhistory add constraint fk_measurementhistory_provenance_id_provenance_pk foreign key (provenance_id) references provenance;
alter table measurementhistory add constraint fk_measurementhistory_station_id_station_pk foreign key (station_id) references station;
alter table measurementhistory add constraint fk_measurementhistory_type_id_type_pk foreign key (type_id) references type;
alter table measurementjson add constraint fk_measurementjson_provenance_id_provenance_pk foreign key (provenance_id) references provenance;
alter table measurementjson add constraint fk_measurementjson_station_id_station_pk foreign key (station_id) references station;
alter table measurementjson add constraint fk_measurementjson_type_id_type_pk foreign key (type_id) references type;
alter table measurementjsonhistory add constraint fk_measurementjsonhistory_provenance_id_provenance_pk foreign key (provenance_id) references provenance;
alter table measurementjsonhistory add constraint fk_measurementjsonhistory_station_id_station_pk foreign key (station_id) references station;
alter table measurementjsonhistory add constraint fk_measurementjsonhistory_type_id_type_pk foreign key (type_id) references type;
alter table measurementstring add constraint fk_measurementstring_provenance_id_provenance_pk foreign key (provenance_id) references provenance;
alter table measurementstring add constraint fk_measurementstring_station_id_station_pk foreign key (station_id) references station;
alter table measurementstring add constraint fk_measurementstring_type_id_type_pk foreign key (type_id) references type;
alter table measurementstringhistory add constraint fk_measurementstringhistory_provenance_id_provenance_pk foreign key (provenance_id) references provenance;
alter table measurementstringhistory add constraint fk_measurementstringhistory_station_id_station_pk foreign key (station_id) references station;
alter table measurementstringhistory add constraint fk_measurementstringhistory_type_id_type_pk foreign key (type_id) references type;
alter table metadata add constraint fk_metadata_station_id_station_pk foreign key (station_id) references station;
alter table station add constraint fk_station_meta_data_id_metadata_pk foreign key (meta_data_id) references metadata;
alter table station add constraint fk_station_parent_id_station_pk foreign key (parent_id) references station;
alter table type add constraint fk_type_meta_data_id_type_metadata_pk foreign key (meta_data_id) references type_metadata;
alter table type_metadata add constraint fk_type_metadata_type_id_type_pk foreign key (type_id) references type;

create index idx_measurement_timestamp on measurement (timestamp desc);
create index idx_measurementjson_timestamp on measurementjson (timestamp desc);
create index idx_measurementstring_timestamp on measurementstring (timestamp desc);

