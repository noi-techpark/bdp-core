#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

set -xeuo pipefail

./exec4all.sh mvn clean site

TMP=$(mktemp -d /tmp/bdpcoredoc.XXXXXX)

wkhtmltopdf dto/target/site/dependencies.html "$TMP/dto-dependencies.pdf"
wkhtmltopdf dc-interface/target/site/dependencies.html "$TMP/dc-interface-dependencies.pdf"
wkhtmltopdf writer/target/site/dependencies.html "$TMP/writer-dependencies.pdf"

pdfunite "$TMP"/*.pdf dependencies.pdf

echo RESULT: See dependencies.pdf...
exit 0

