# BIG DATA PLATFORM - Schema Generator

Generate a schema dump of all entities inside a given path and write it into a file and to stdout.
The output patterns are defined inside the implicit naming strategy class.

For details see code comments.

## Compilation:
  `mvn clean package`

## Example execution:
Dump BDP/dal entity classes:
```
    java -cp '../dto/target/dto-2.0.0.jar:../dal/target/dal-2.0.0.jar:target/schemagenerator-1.0.0.jar' \
         -Dlog4j.configuration=src/main/resources/log4j.properties \
         it.bz.idm.bdp.tools.SchemaGenerator \
         it.bz.idm.bdp.dal \
         /tmp/schema_dump1.sql
```

Hereby, `../dto/target/dto-2.0.0.jar:../dal/target/dal-2.0.0.jar` are class path entries that contain
the `@Entity` annotated classes, that should be dumped as SQL DDL commands.  `target/schemagenerator-1.0.0.jar`
is the library that contains the `main` method.  `-Dlog4j.configuration=src/main/resources/log4j.properties` is
to override log levels, which may be too verbose.  `it.bz.idm.bdp.tools.SchemaGenerator` is the class that
contains the main method to be executed.  The script scans `it.bz.idm.bdp.dal` for entity classes, and dumps the
result into `/tmp/schema_dump1.sql`.

## Known issues
You can provide PostgreSQL connection information. However, the script also works if they are wrong.  That is,
because of how the registry builder works. The registry builder needs a data source, however, also if the
connection fails it succeeds in building a DDL SQL script.  The drawback hereby is, that it generates a warning
on stderr, which cannot be avoided with a try-catch...
