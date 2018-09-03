#!/bin/bash

set -xeuo pipefail

CMD="$@"

find reader/ writer/ dal/ dto/ dc-interface/ ws-interface/ -maxdepth 0 -type d -exec sh -c "cd \"{}\" && $CMD" \;

exit 0

