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
DTO_VERSION_FOR_WS="3.1.0"

# Add -SNAPSHOT to each version tag
test "$TYPE" = "snapshot" && {
    VERSION="$VERSION-SNAPSHOT"
    DTO_VERSION_FOR_WS="$DTO_VERSION_FOR_WS-SNAPSHOT"
}

# UPDATE pom.xml files
for FOLDER in dto dal reader writer dc-interface ws-interface
do
    $CMD -u "/pom:project/pom:repositories/pom:repository[starts-with(pom:id,'$REP')]/pom:url" -v $REP_URL $FOLDER/pom.xml
    $CMD -u "/pom:project/pom:repositories/pom:repository[starts-with(pom:id,'$REP')]/pom:id" -v $REP_ID $FOLDER/pom.xml

    $CMD -u "/pom:project/pom:version" -v $VERSION $FOLDER/pom.xml
    $CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dto']/pom:version" -v $VERSION $FOLDER/pom.xml
    $CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dal']/pom:version" -v $VERSION $FOLDER/pom.xml
done

# WARNING: ws-interface must always use a fixed DTO package version (DTO_VERSION_FOR_WS)
#
# The newer dto versions are just for the dc-interface and writer. This is
# because the metadata gets flattened and injected directly at level 1 of the
# JSON hierarchy. However, this could overwrite existing json attributes, which
# happens with data collectors sometimes. The solution is now to keep an old
# version for webservices, where the API is stable and just update it for data
# collectors, which can be adapted easily.
$CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dto']/pom:version" -v $DTO_VERSION_FOR_WS $FOLDER/pom.xml

# UPDATING CONTRIBUTORS.rst...
bash CONTRIBUTORS.rst

exit 0
