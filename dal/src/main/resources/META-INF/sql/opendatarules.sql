begin;
delete from bdprules ru  where ru.role_id in (select id from bdprole where name='GUEST');
insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'GUEST' and s.stationtype not in ('TrafficSensor','Environmentstation');
refresh materialized view bdppermissions;
commit;
