#!/bin/bash
set -xeuo pipefail

DEST=/tmp/schema-migration-1.1.0-2.0.0

# Make sure all needed parameters are set. This will exit the script, if not!
PGUSER=$PGUSER
PGPASSWORD=$PGPASSWORD
PGHOST=$PGHOST
PGDATABASE=$PGDATABASE
PGPORT=$PGPORT

# All output should be in English
export LC_ALL=C

mkdir -p "$DEST"
sed 's/intime/intimev2/' schema-2.0.0-dump.sql > $DEST/dump.sql
sed 's/intime/intimev2/' schema-2.0.0-modifications.sql > $DEST/modi.sql

psql -v ON_ERROR_STOP=1 --echo-all -f $DEST/dump.sql
psql -v ON_ERROR_STOP=1 --echo-all -f $DEST/modi.
psql -v ON_ERROR_STOP=1 --echo-all -f schema-migration-1.1.0-2.0.0.sql

echo "DONE. Check if everything is set correctly, and if you are done rename"
echo "      the schema intime to intimev1 and intimev2 to intime."

exit 0
