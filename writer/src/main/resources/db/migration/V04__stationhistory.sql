create sequence stationhistory_seq start 1 increment 1;
create table stationhistory (
  id int8 default nextval('stationhistory_seq') not null,
  station_id int8 not null,
  created_on timestamp not null,
  active boolean,
  name varchar(255) not null,
  origin varchar(255) not null,
  pointprojection geometry,
  stationcode varchar(255) not null,
  stationtype varchar(255) not null,
  meta_data_id int8,
  parent_id int8,
  primary key (id),
  constraint uc_stationhistory_time unique (station_id, created_on),
  constraint fk_stationhistory_station foreign key (station_id) references station,
  constraint fk_stationhistory_parent foreign key (parent_id) references station,
  constraint fk_stationhistory_meta_data_id foreign key (meta_data_id) references metadata 
);
  
create index idx_stationhistory_station_time on stationhistory (station_id, created_on desc);
