-- SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
--
-- SPDX-License-Identifier: AGPL-3.0-or-later

set search_path to ${default_schema}, public;

create index IDX_METADATA_HISTORY on metadata(station_id, created_on);
