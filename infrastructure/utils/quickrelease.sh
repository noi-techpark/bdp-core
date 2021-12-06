#!/bin/bash
set -euo pipefail

TYPE="$1"
VERSION="$2"

test "$TYPE" = "release" -o "$TYPE" = "snapshot" || {
    echo "ERROR: \$1 must be either 'release' or 'snapshot'"
    exit 1
}

### Configuration
REP="maven-repo.opendatahub.bz.it"
REP_ID="$REP-$TYPE"
REP_URL="http://it.bz.opendatahub.s3-website-eu-west-1.amazonaws.com/$TYPE"
XMLNS=http://maven.apache.org/POM/4.0.0
CMD="xmlstarlet ed -P -L -N pom=$XMLNS"

# Add -SNAPSHOT to each version tag
test "$TYPE" = "snapshot" && {
    VERSION="$VERSION-SNAPSHOT"
}

# UPDATE pom.xml files
for FOLDER in dto writer dc-interface
do
    $CMD -u "/pom:project/pom:repositories/pom:repository[starts-with(pom:id,'$REP')]/pom:url" -v "$REP_URL" $FOLDER/pom.xml
    $CMD -u "/pom:project/pom:repositories/pom:repository[starts-with(pom:id,'$REP')]/pom:id" -v "$REP_ID" $FOLDER/pom.xml

    $CMD -u "/pom:project/pom:version" -v "$VERSION" $FOLDER/pom.xml
    $CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dto']/pom:version" -v "$VERSION" $FOLDER/pom.xml
done

# UPDATING CONTRIBUTORS.rst...
bash CONTRIBUTORS.rst

exit 0
