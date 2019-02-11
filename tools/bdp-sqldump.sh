#!/bin/bash
set -euo pipefail

VERSION="1.0.0"

echo
echo "## BIG DATA PLATFORM -- SQL GENERATOR v$VERSION ##"
echo 

test $# -eq 1 || {
    echo "USAGE: $0 OUTPUTVERSION"
    echo "  The OUTPUTVERSION must correspond to DTO and DAL versions (see their pom.xml files)."
    echo
    echo "  Example: $0 2.0.0"
    echo
    exit 1
}

# Root directory of your bdp-core source
BDPROOT=$(realpath $(pwd)/..)

GENERATORJAR="target/schemagenerator-$VERSION.jar"
GENERATORMAIN='it.bz.idm.bdp.tools.SchemaGenerator'
OUTPUTPATH="$BDPROOT/dal/src/main/resources/META-INF/sql"

# Re-assign variables, to be sure they are not "unbound", OUTPUTVERSION must correspond to DAL and DTO versions
OUTPUTVERSION=$1

CLASSPATH="../dto/target/dto-$OUTPUTVERSION.jar:../dal/target/dal-$OUTPUTVERSION.jar"
CLASSPREFIX="it.bz.idm.bdp.dal"
OUTPUTFILE="$OUTPUTPATH/schema-$OUTPUTVERSION-dump.sql"

# Do not overwrite existing files, move them to .backup
EXISTED=""
test -f "$OUTPUTFILE" && {
    mv "$OUTPUTFILE" "$OUTPUTPATH/schema-$OUTPUTVERSION-dump.sql.backup"
    echo "File '$OUTPUTFILE' exists, we move the original one to $OUTPUTPATH/schema-$OUTPUTVERSION-dump.sql.backup"
}

# Build the jar file, if it does not yet exist
test -f "$GENERATORJAR" || mvn clean package

# Separate multiple .jar files defined inside parameter $1 with : (colon)
# We override log4j properties to minimize verbose outputs
# A known issue throws always exceptions, therefore we pipe it to /dev/null
java -cp "$CLASSPATH:$GENERATORJAR" -Dlog4j.configuration=src/main/resources/log4j.properties $GENERATORMAIN $CLASSPREFIX $OUTPUTFILE 2>/dev/null || {
    echo "FAILED: An error occurred - Response code:" $?
}

echo 
exit 0
