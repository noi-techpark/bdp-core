#!/bin/bash

set -euo pipefail

VERSION="$1"
XMLNS=http://maven.apache.org/POM/4.0.0
CMD="xmlstarlet ed -P -L -N pom=$XMLNS"

$CMD -u "/pom:project/pom:version" -v $VERSION dal/pom.xml
$CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dto']/pom:version" -v $VERSION dal/pom.xml

$CMD -u "/pom:project/pom:version" -v $VERSION dc-interface/pom.xml
$CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dto']/pom:version" -v $VERSION dc-interface/pom.xml

$CMD -u "/pom:project/pom:version" -v $VERSION dto/pom.xml

$CMD -u "/pom:project/pom:version" -v $VERSION reader/pom.xml
$CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dal']/pom:version" -v $VERSION reader/pom.xml

$CMD -u "/pom:project/pom:version" -v $VERSION writer/pom.xml
$CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dal']/pom:version" -v $VERSION writer/pom.xml

$CMD -u "/pom:project/pom:version" -v $VERSION ws-interface/pom.xml
$CMD -u "/pom:project/pom:dependencies/pom:dependency[pom:groupId='it.bz.idm.bdp'][pom:artifactId='dto']/pom:version" -v $VERSION ws-interface/pom.xml

exit 0
