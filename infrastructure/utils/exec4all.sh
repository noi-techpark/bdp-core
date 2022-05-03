#!/bin/bash

set -xeuo pipefail

CMD="$@"

find dto/ writer/ dc-interface/ -maxdepth 0 -type d -exec sh -c "cd \"{}\" && $CMD" \;

exit 0

