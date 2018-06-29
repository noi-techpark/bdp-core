insert into bdprules (role_id,station_id) select r.id,s.id from  station s, bdprole r where r.name = 'GUEST' and s.stationtype not in ('TrafficSensor','Environmentstation');
