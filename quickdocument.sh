#!/bin/bash

set -xeuo pipefail

./exec4all.sh mvn clean site

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

