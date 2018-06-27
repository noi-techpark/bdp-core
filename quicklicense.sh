#!/bin/bash

set -xeuo pipefail

cd dto && mvn license:format
cd ../dal && mvn license:format
cd ../dc-interface && mvn license:format
cd ../ws-interface && mvn license:format
cd ../writer && mvn license:format
cd ../reader && mvn license:format
cd ..

exit 0

