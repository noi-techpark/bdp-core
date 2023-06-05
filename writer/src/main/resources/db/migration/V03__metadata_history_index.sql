-- SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
--
-- SPDX-License-Identifier: AGPL-3.0-or-later

/*
 * Do not use varchar(n) which just causes issues and is slower on inserts than text
 * See http://www.postgresql.org/docs/current/static/datatype-character.html
 */
set search_path to ${default_schema}, public;

create index IDX_METADATA_HISTORY on metadata(station_id, created_on);
