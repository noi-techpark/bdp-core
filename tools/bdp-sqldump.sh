#!/bin/bash
set -euo pipefail

VERSION="1.0.0"

echo
echo "## BIG DATA PLATFORM -- SQL GENERATOR v$VERSION ##"
echo 
echo "NB: This script must be executed from the BDP-CORE root directory!"
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
BDPROOT=$(pwd)

# Re-assign variables, to be sure they are not "unbound", OUTPUTVERSION must correspond to DAL and DTO versions
OUTPUTVERSION=$1

# Jar files and classpath
GEN="$BDPROOT/tools/target/schemagenerator-$VERSION.jar"
DTO="$BDPROOT/dto/target/dto-$OUTPUTVERSION.jar"
DAL="$BDPROOT/dal/target/dal-$OUTPUTVERSION.jar"
CLASSPATH="$DTO:$DAL:$GEN"

# Classes and paths
GENERATORMAIN='it.bz.idm.bdp.tools.SchemaGenerator'
CLASSPREFIX="it.bz.idm.bdp.dal"
STRATEGYCLASS='it.bz.idm.bdp.dal.util.SchemaGeneratorImplicitNamingStrategy'

# Output
OUTPUTPATH="$BDPROOT/dal/src/main/resources/META-INF/sql"
OUTPUTFILE="$OUTPUTPATH/schema-$OUTPUTVERSION-dump.sql"

cd tools
mvn clean package
cd -
test -f "$GEN" || {
    echo "ERROR: $GEN not found. Maybe you have a version mismatch with $VERSION. See:"
    ls $(dirname $GEN)/*.jar
    exit 1
}

cd dto
mvn clean package
cd -
test -f "$DTO" || {
    echo "ERROR: $DTO not found. Maybe you have a version mismatch with $OUTPUTVERSION. See:"
    ls $(dirname $DTO)/*.jar
    exit 1
}

cd dal
mvn clean package
cd -
test -f "$DAL" || {
    echo "ERROR: $DAL not found. Maybe you have a version mismatch with $OUTPUTVERSION. See:"
    ls $(dirname $DAL)/*.jar
    exit 1
}

# Do not overwrite existing files, move them to .backup
EXISTED=""
test -f "$OUTPUTFILE" && {
    mv "$OUTPUTFILE" "$OUTPUTPATH/schema-$OUTPUTVERSION-dump.sql.backup"
    echo "File '$OUTPUTFILE' exists, we move the original one to $OUTPUTPATH/schema-$OUTPUTVERSION-dump.sql.backup"
}

# Separate multiple .jar files defined inside parameter $1 with : (colon)
# We override log4j properties to minimize verbose outputs
# A known issue throws always exceptions, therefore we pipe it to /dev/null
cd tools
temp_file=$(mktemp)
java -cp "$CLASSPATH" -Dlog4j.configuration=src/main/resources/log4j.properties \
        $GENERATORMAIN $CLASSPREFIX $STRATEGYCLASS $OUTPUTFILE 2>temp_file || {
    RES=$?
    echo "ERROR: Response code:" $RES
    echo "ERROR: Message:"
    cat temp_file
    rm temp_file
    cd -
    exit $RES
}

cd -

echo 
exit 0
