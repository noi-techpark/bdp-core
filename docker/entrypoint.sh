#!/bin/bash

mkdir -p ~/.m2
echo "<settings><localRepository>$PWD/tmp/.m2</localRepository></settings>" > ~/.m2/settings.xml

/bin/bash -c "$@"
