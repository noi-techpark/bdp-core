#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

set -euo pipefail

TYPE="$1"
VERSION="$2"

test "$TYPE" = "release" -o "$TYPE" = "snapshot" || {
    echo "ERROR: \$1 must be either 'release' or 'snapshot'"
    exit 1
}

### Configuration
REP_ID="maven-repo.opendatahub.bz.it"
REP_URL="https://maven-repo.opendatahub.com/$TYPE"
XMLNS=http://maven.apache.org/POM/4.0.0
CMD="xmlstarlet ed -P -L -N pom=$XMLNS"

# Add -SNAPSHOT to each version tag
test "$TYPE" = "snapshot" && {
    VERSION="$VERSION-SNAPSHOT"
}

# Parent pom.xml inside root-folder
$CMD -u "/pom:project/pom:version" -v "$VERSION" pom.xml
$CMD -u "/pom:project/pom:properties/pom:revision" -v "$VERSION" pom.xml
$CMD -u "/pom:project/pom:repositories/pom:repository[starts-with(pom:id,'$REP_ID')]/pom:url" -v "$REP_URL" pom.xml

# UPDATE pom.xml files
for FOLDER in dto writer dc-interface
do
    $CMD -u "/pom:project/pom:parent/pom:version" -v "$VERSION" $FOLDER/pom.xml
    $CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dto']/pom:version" -v "$VERSION" $FOLDER/pom.xml
done

exit 0
