#!/bin/bash

set -xeuo pipefail

CMD="$@"

find dto/ dal/ reader/ writer/ dc-interface/ ws-interface/ -maxdepth 0 -type d -exec sh -c "cd \"{}\" && $CMD" \;

exit 0

