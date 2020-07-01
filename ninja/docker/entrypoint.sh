#!/bin/sh

mkdir -p ~/.m2
cat > ~/.m2/settings.xml << EOF
<settings>
    <localRepository>$PWD/docker/.m2</localRepository>
</settings>
EOF

export MAVEN_CONFIG="$HOME"

/usr/local/bin/mvn-entrypoint.sh $@
