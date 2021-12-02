# DAL: SQL FOLDER

This folder contains sql scripts, which modify the database tables and content.
That is, it does what ORM tools cannot accomplish automatically, or where ORM
fails to perform well.

Do not just commit database schema dumps, but rather treat SQL-DDL files as
source code and cleanly distinguish the initial creation and later updates.
(see https://opendatahub.readthedocs.io/en/latest/guidelines/database.html)

### FILES WITH PATTERN `schema-<version>-<type>.sql`

... are meant to be used for new `bdp-core` installations (also the
quickbuild.sh script uses them). If you want to install bdp-core with version
`1.0.2` for instance, run first `schema-1.0.2-dump.sql` and then
`schema-1.0.2-modifications.sql`. The former contains a `pg_dump` output after
Hibernate created all artefacts inside PostgreSQL, the latter modifies that
schema to hold also materialized views, some indexes and inserts initial data.

### `opendatarules.sql`

...contains insert statements to declare some data as accessible without
authentication, that is, declares it OPEN DATA.

### FILES WITH PATTERN `schema-update-<oldversion>-<newversion>.sql`

.. contain a script, to update from `oldversion` to a new `newversion` schema.
These scripts should be used if you want to update an existing `bdp-core`
installation.

For more details, visit our official documentation under
http://opendatahub.readthedocs.io/en/latest/howto/development.html
