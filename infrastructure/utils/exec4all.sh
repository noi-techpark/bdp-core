#!/bin/bash

# SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
#
# SPDX-License-Identifier: CC0-1.0

set -xeuo pipefail

CMD="$@"

find dto/ writer/ dc-interface/ -maxdepth 0 -type d -exec sh -c "cd \"{}\" && $CMD" \;

exit 0

