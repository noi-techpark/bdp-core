
set search_path to ${default_schema}, public;

create index if not exists IDX_STATION_PARENT on station(parent_id);
