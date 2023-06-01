<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# BIG DATA PLATFORM - Schema Generator

Generate a schema dump of all entities inside a given path and write it into a file.

## Short version
To generate a new schema, execute the shell script `bdp-sqldump.sh`.

## Detailed version

This chapter is about the Java tool itself, the shell script above simplifies these steps.

The output patterns, that define how identifiers are written, are defined inside the implicit naming strategy class.
For example, any unique constraint starts with `uc_` and contains the current table and used columns, all lower case
with underscores.  For more details see code comments.

### Compilation:
  `mvn clean package`

### Example execution:
Configure the PostgreSQL connection via environmental variables:
* `ODH_SG_SERVER` - database server name (optional, default = localhost)
* `ODH_SG_DBNAME` - database name
* `ODH_SG_USER`   - database user (optional, default = postgres)
* `ODH_SG_PASSWD` - database password
NB: This configuration is not needed, you can also leave it unset, but it will generate a warning. See
[Known issues](#known-issues) for details.

Dump BDP/dal entity classes:
```
    java -cp '../dto/target/dto-2.0.0.jar:../dal/target/dal-2.0.0.jar:target/schemagenerator-1.0.0.jar' \
         -Dlog4j.configuration=src/main/resources/log4j.properties \
         it.bz.idm.bdp.tools.SchemaGenerator \
         it.bz.idm.bdp.dal \
         it.bz.idm.bdp.dal.util.SchemaGeneratorImplicitNamingStrategy \
         /tmp/schema_dump1.sql
```

Hereby, `../dto/target/dto-2.0.0.jar:../dal/target/dal-2.0.0.jar` are class path entries that contain
the `@Entity` annotated classes, that should be dumped as SQL DDL commands.  `target/schemagenerator-1.0.0.jar`
is the library that contains the `main` method.  `-Dlog4j.configuration=src/main/resources/log4j.properties` is
to override log levels, which may be too verbose.  `it.bz.idm.bdp.tools.SchemaGenerator` is the class that
contains the main method to be executed.  The script scans `it.bz.idm.bdp.dal` for entity classes, and dumps the
result configured by `it.bz.idm.bdp.dal.util.SchemaGeneratorImplicitNamingStrategy` into `/tmp/schema_dump1.sql`.

### Known issues
You can provide PostgreSQL connection information. However, the script also works if they are wrong.  That is,
because of how the registry builder works. The registry builder needs a data source, however, also if the
connection fails it succeeds in building a DDL SQL script.  The drawback hereby is, that it generates a warning
on stderr, which cannot be avoided with a try-catch...
