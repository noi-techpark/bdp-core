select count(*) from bdppermissions;
select count(*) from measurement;




explain analyze
select *
from intime.measurement measuremen0_
cross join intime.bdppermissions bdppermiss1_
where (measuremen0_.station_id=bdppermiss1_.station_id or bdppermiss1_.station_id is null) and (measuremen0_.type_id=bdppermiss1_.type_id or bdppermiss1_.type_id is null) and (measuremen0_.period=bdppermiss1_.period or bdppermiss1_.period is null) and bdppermiss1_.role_id=1
order by timestamp DESC
limit 1;





-- Compare tables (if result is empty, tables are equal)
WITH
    test AS (table bdppermissions_oldtable),
    test2 AS (table bdppermissions)
SELECT * FROM (
    (TABLE test EXCEPT ALL TABLE test2)
    UNION
    (TABLE test2 EXCEPT ALL TABLE test)
) d;

-- if equal, drop old table
drop table bdppermissions_oldtable;
