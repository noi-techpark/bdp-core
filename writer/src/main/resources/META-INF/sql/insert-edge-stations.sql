select * from station where stationtype = 'Linkstation';

insert into station(active, available, name, stationcode, stationtype) 
with 
a as (
	select id, name from station where stationcode = 'A22_ML107'
),
b as (
	select id, name from station where stationcode = 'A22_ML103'
)
select true, true, a.name || '->' || b.name, a.name || '->' || b.name, 'Linkstation'
from a, b
union
select true, true, b.name || '->' || a.name, b.name || '->' || a.name, 'Linkstation'
from a, b;

select * from edge;

insert into edge (origin_id, destination_id, edge_data_id)
with 
a as (
	select id, name from station where stationcode = 'A22_ML107'
),
b as (
	select id, name from station where stationcode = 'A22_ML103'
)
select a.id origin, b.id destination, (select id from station where stationcode = a.name || '->' || b.name) as edge
from a, b
union
select b.id origin, a.id destination, (select id from station where stationcode = b.name || '->' || a.name) as edge
from a, b;




