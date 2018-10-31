#!/bin/bash

mkdir -p ~/.m2

cat > ~/.m2/settings.xml << EOF
<settings>
    <localRepository>$PWD/docker/.m2</localRepository>
</settings>
EOF

/bin/bash -c "$@"
