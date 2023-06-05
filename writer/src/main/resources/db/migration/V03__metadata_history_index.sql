
set search_path to ${default_schema}, public;

create index IDX_METADATA_HISTORY on metadata(station_id, created_on);
