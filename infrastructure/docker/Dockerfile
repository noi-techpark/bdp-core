#
# writer - Data Writer for the Big Data Platform
#
# Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
# Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program (see LICENSES/GPL-3.0.txt). If not, see
# <http://www.gnu.org/licenses/>.
#
# SPDX-License-Identifier: GPL-3.0
#

FROM maven:3-jdk-8-alpine as base

RUN mkdir -p /code

# Dev
FROM base as dev

# Build
FROM base as build

WORKDIR /code
COPY dto /code/dto
RUN cd dto && mvn -B -U -DskipTests install && rm -rf dto
COPY writer/src /code/src
COPY writer/pom.xml /code/
RUN mvn package -DfinalName=ROOT

# Archive
FROM tomcat:8.5-jdk8-openjdk-slim-buster as archive
COPY --from=build /code/target/writer.war /usr/local/tomcat/webapps/ROOT.war

# Run
FROM archive as run
CMD ["catalina.sh", "run"]
