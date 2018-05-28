#!/bin/bash

set -xeuo pipefail

cd dto && mvn clean site
cd ../dal && mvn clean site
cd ../dc-interface && mvn clean site
cd ../ws-interface && mvn clean site
cd ../writer && mvn clean site
cd ../reader && mvn clean site
cd ..

TMP=$(mktemp -d /tmp/bdpcoredoc.XXXXXX)

wkhtmltopdf dto/target/site/dependencies.html $TMP/dto-dependencies.pdf
wkhtmltopdf dal/target/site/dependencies.html $TMP/dal-dependencies.pdf
wkhtmltopdf dc-interface/target/site/dependencies.html $TMP/dc-interface-dependencies.pdf
wkhtmltopdf ws-interface/target/site/dependencies.html $TMP/ws-interface-dependencies.pdf
wkhtmltopdf writer/target/site/dependencies.html $TMP/writer-dependencies.pdf
wkhtmltopdf reader/target/site/dependencies.html $TMP/reader-dependencies.pdf

pdfunite $TMP/*.pdf dependencies.pdf

echo RESULT: See dependencies.pdf...
exit 0

