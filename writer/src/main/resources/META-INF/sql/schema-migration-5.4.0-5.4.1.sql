ALTER TABLE intimev2."event" ADD provenance_id int8 NULL;
alter table event add constraint fk_event_provenance_id_provenance_pk foreign key (provenance_id) references provenance;
