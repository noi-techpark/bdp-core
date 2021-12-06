#!/bin/bash

###############################################################################
# WARNING
# This script is provided AS IS and has been tested on a Ubuntu 18.04, Mint 19.1
# and Debian 9. If you are using it on a different Linux distro or on a different
# platform, you must verify the correctness of paths on your workstation and
# that the commands used are the same on your PC.
# You need to provide your password during the execution of this script.
#
# Prerequisites:
#   - sudo
#   - postgresql-9.5 (or higher)
#   - postgis
#   - pgcrypto
#   - xmlstarlet
#   - maven
#   - openjdk-8-jdk
#   - tomcat8
#
# This script sets up your workstation to develop core components of the BDP.
# More information about this script and a complete guide to its components
# can be found in its official documentation:
#
# http://opendatahub.readthedocs.io/en/latest/howto/development.html
#
# You must set the following environmental variables before execution:
# - PGUSER - Postgres user, that has privileges to create schemas and tables
# - PGPASSWORD - Postgres user's password
# - SQLVERSION - Schema version of dump and modification scripts inside DAL
#
###############################################################################

set -euo pipefail

###############################################################################
# CONFIGURATION
###############################################################################

# Root directory of your bdp-core source
BDPROOT=$(pwd)

# Log files folder
LOGS=/var/log/opendatahub
LOGSDC=$LOGS/data-collectors
LOGSWS=$LOGS/webservices

# Java
JAVA_VERSION=1.8

# Log files owner/group
# If you use tomcat as standalone installation, choose user tomcat or tomcat8,
# depending on your system. $USER should be choosen, if you start the
# server from an IDE (ex., Eclipse). If you use any other application server,
# make sure that logs are writeable for that instance.
APPSERVER=tomcat8
APPSERVER_USER=tomcat8
APPSERVER_GROUP=tomcat8
APPSERVER_WEBAPPS=/var/lib/tomcat8/webapps

# -- Postgres config ----------------------------------------------------------
# PGUSER is a role in Postgres with createdb and create-user privileges
# PGPASSWORD is the password for the above role
# ...both must be set outside this script (reassigning here to check for
#    unbound variables)
PGUSER=$PGUSER
PGPASSWORD=$PGPASSWORD

# PGUSER1 is a role in Postgres with write access
PGUSER1=bdp_readwrite
PGPASS1=bdp

# PGUSER2 is a role in Postgres with read-only access
PGUSER2=bdp_readonly
PGPASS2=bdpreadonly

# We will create a new database $PGDBNAME for you inside your Postgres instance
PGDBNAME=bdptest
PGPORT=5432
PGSCHEMA=intime333

# Which database should be used for version tests and other read-only
# operations, before installing the new database $PGDBNAME. Please not, your
# user $PGUSER must have read access to that database.
PGDBDEFAULT=postgres

# The persistence.xml file is used for db access and contains all PG* variables above
PXMLFILE=$BDPROOT/dal/src/main/resources/META-INF/persistence.xml

# Schema dump version, that we want to use for this build
SQLVERSION=$SQLVERSION

# DUMPSQL is what we've got from pg_dump after a Hibernate create execution
DUMPSQL=$BDPROOT/dal/src/main/resources/META-INF/sql/schema-$SQLVERSION-dump.sql

# MODSSQL is what we need to modify manually, to have a complete usable schema
# for bdp-core to run correctly.
MODSSQL=$BDPROOT/dal/src/main/resources/META-INF/sql/schema-$SQLVERSION-modifications.sql


###############################################################################
# EXECUTION (Do not change anything below this line)
###############################################################################

echo_bold() {
    tput bold
    echo "$1"
    tput sgr0
}

psql_call() {
    # PGUSER and PGPASSWORD must be set outside the script
    psql -v ON_ERROR_STOP=1 -p $PGPORT -h localhost "$@"
}

psql_createuser() {
    psql_call -d $PGDBNAME -tc "SELECT 1 FROM pg_roles WHERE rolname = '$1'" | grep -q 1 && {
        echo "WARNING: Role $1 already exists, skipping..."
        return
    }

    psql_call -d $PGDBNAME -c "CREATE USER $1 WITH LOGIN NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;"

    psql_call -d $PGDBNAME << EOF
        ALTER ROLE $1 PASSWORD '$2';
        GRANT CONNECT ON DATABASE $PGDBNAME TO $1;
        COMMENT ON ROLE $1 IS '$3';
EOF
}

psql_createdb() {
    psql_call -d $PGDBDEFAULT -tc "SELECT 1 FROM pg_database WHERE datname = '$PGDBNAME'" | grep -q 1 || \
        psql_call -d $PGDBDEFAULT -c "CREATE DATABASE $PGDBNAME"
}

psql_createschema() {
    psql_call -d $PGDBNAME -c "CREATE SCHEMA $PGSCHEMA"
}

psql_createextension() {
    psql_call -d $PGDBNAME -c "CREATE EXTENSION IF NOT EXISTS $1"
}


psql_dropdb() {
    psql_call -d $PGDBDEFAULT -c "DROP DATABASE IF EXISTS $PGDBNAME"
}

give_consent() {
    # make sure user knows limits of the script
    echo
    echo_bold "!!! BDP-CORE SETUP !!!"
    echo
    echo "This script sets up the BDP on this workstation. For a thourough"
    echo "description of this script check also the official documentation at:"
    echo
    echo "http://opendatahub.readthedocs.io/en/latest/howto/development.html"
    echo
    echo "Please make sure that you:"
    echo "- run this script from the directory in which you clone the repository"
    echo "- modify the variables in this script according to your needs & local setup"
    echo
    echo "We tested it on Ubuntu 18.04 and Debian 9."
    tput bold
    echo
    read -p "Please write 'yes' to continue or any other character to exit: "  answer
    echo
    tput sgr0
}

db_setup_import_sqlfiles() {
    echo
    echo_bold "Postgres: Import dumped SQL files"
    echo

    # We need also "public" for previously installed PostGIS data types, i.e., geometry...
	(echo "SET search_path TO $PGSCHEMA, public;"; cat $DUMPSQL) | psql_call -d $PGDBNAME -1 -f -
	(echo "SET search_path TO $PGSCHEMA, public;"; cat $MODSSQL) | psql_call -d $PGDBNAME -1 -f -

	echo_bold "...DONE."
}

db_setup_privileges() {
    echo
    echo_bold "Postgres: Grant privileges to USERS"
    echo

    psql_call -d $PGDBNAME -c "GRANT ALL ON SCHEMA $PGSCHEMA TO $PGUSER1"
    psql_call -d $PGDBNAME -c "GRANT USAGE ON SCHEMA $PGSCHEMA TO $PGUSER2"
    psql_call -d $PGDBNAME -c "GRANT ALL ON ALL TABLES IN SCHEMA $PGSCHEMA TO $PGUSER1"
    psql_call -d $PGDBNAME -c "GRANT ALL ON ALL SEQUENCES IN SCHEMA $PGSCHEMA TO $PGUSER1"
    psql_call -d $PGDBNAME -c "GRANT SELECT ON ALL TABLES IN SCHEMA $PGSCHEMA TO $PGUSER2"
    psql_call -d $PGDBNAME -c "GRANT SELECT ON ALL SEQUENCES IN SCHEMA $PGSCHEMA TO $PGUSER2"

    echo_bold "...DONE."
}

db_setup_db_and_users() {
    echo
    echo_bold "Postgres: Create DB and USERS"
    echo

    # Look at https://wiki.postgresql.org/wiki/First_steps
    psql_createdb

    psql_createschema

    psql_createextension "pgcrypto"
    psql_createextension "postgis"

    psql_createuser $PGUSER1 $PGPASS1 'Big Data Platform user for write access'
    psql_createuser $PGUSER2 $PGPASS2 'Big Data Platform user for read-only access'

    echo_bold "...DONE."
}

create_log_files() {
    echo
    echo_bold "Log file permissions"
    echo

    sudo mkdir -p $LOGS/webservices
    sudo mkdir -p $LOGS/data-collectors
    sudo touch $LOGS/writer.log
    sudo touch $LOGS/reader.log
    sudo chown -R $APPSERVER_USER:$APPSERVER_GROUP $LOGS

    echo_bold "...DONE."
}

update_persistence() {
    echo
    echo_bold "Update values in persistence.xml"
    echo

    cp "$PXMLFILE.dist" $PXMLFILE

    # values common for reader and writer
    xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.default_schema']/@value" -v ${PGSCHEMA} $PXMLFILE
    xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.hikari.dataSource.serverName']/@value" -v 'localhost' $PXMLFILE
    xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.hikari.dataSource.portNumber']/@value" -v ${PGPORT} $PXMLFILE
    xmlstarlet ed -L -u "//_:persistence-unit/_:properties/_:property[@name='hibernate.hikari.dataSource.databaseName']/@value" -v ${PGDBNAME} $PXMLFILE

    # update values for reader
    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence']/_:properties/_:property[@name='hibernate.hikari.dataSource.user']/@value" -v ${PGUSER2} $PXMLFILE
    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence']/_:properties/_:property[@name='hibernate.hikari.dataSource.password']/@value" -v ${PGPASS2} $PXMLFILE

    # update values for writer
    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence-write']/_:properties/_:property[@name='hibernate.hikari.dataSource.user']/@value" -v ${PGUSER1} $PXMLFILE
    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence-write']/_:properties/_:property[@name='hibernate.hikari.dataSource.password']/@value" -v ${PGPASS1} $PXMLFILE

    # update value of hibernate property ("validate" on production and "create", if you want to do a new schema dump)
    if [[ ${DUMPFILE+x} ]]; then
		    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence-write']/_:properties/_:property[@name='hibernate.hbm2ddl.auto']/@value" -v "create" $PXMLFILE
	  else
		    xmlstarlet ed -L -u "//_:persistence-unit[@name='jpa-persistence-write']/_:properties/_:property[@name='hibernate.hbm2ddl.auto']/@value" -v "validate" $PXMLFILE
	  fi

    echo_bold "...DONE."
}

final_message() {
    echo
    echo
    echo_bold "!!! The BDP-CORE SETUP is now complete !!!"
    echo
    echo
    echo_bold "The service should be accessible in short time on "
    echo_bold "the address you configured for your application server."
    echo
    echo_bold "To get started, let's insert a new Environmentstation and read it afterwards:"
    echo
    echo_bold "curl -i -X POST -H 'Content-Type:application/json' -d '[{\"id\":\"Hello world\",\"_t\":\"it.bz.idm.bdp.dto.StationDto\"}]' http://127.0.0.1:8080/writer/json/syncStations/Environmentstation"
    echo
    echo_bold "...which sould return: HTTP/1.1 200 Content-Length: 0 Date: ..."
    echo
    echo_bold "curl http://127.0.0.1:8080/reader/stations?stationType=Environmentstation"
    echo
    echo_bold '...which should return ["Hello world"]'.
    echo
    echo_bold "Finally, you can get an access/refresh token with the default admin account:"
    echo
    echo_bold "curl 'http://127.0.0.1:8080/reader/refreshToken?user=admin&password=123456789'"
    echo
    echo_bold "...make sure, you change that password as soon as possible!"
    echo
    echo_bold "See http://opendatahub.readthedocs.io/en/latest/howto/development.html"
    echo
    echo_bold "--> READY."
}


deploy_war_files() {

    echo
    echo_bold "Build artifacts and deploy reader.war and writer.war to $APPSERVER"
    echo

    # Build libs and war files
    cd $BDPROOT

    for FOLDER in dto dal dc-interface ws-interface; do
        cd $FOLDER
        mvn clean install
        cd ..
    done

    for FOLDER in writer reader; do
        cd $FOLDER
        mvn clean package
        cd ..
    done

    sudo cp $BDPROOT/reader/target/reader.war $APPSERVER_WEBAPPS
    sudo cp $BDPROOT/writer/target/writer.war $APPSERVER_WEBAPPS
    sudo chown $APPSERVER_USER:$APPSERVER_GROUP $APPSERVER_WEBAPPS/reader.war
    sudo chown $APPSERVER_USER:$APPSERVER_GROUP $APPSERVER_WEBAPPS/writer.war

    echo
    echo_bold "DONE... restarting $APPSERVER. Please wait!"
    echo

    sudo service $APPSERVER restart

    echo_bold "...DONE."
}

test_installation() {
    MSG_ERR=$1
    shift
    CMD="$@"
    echo -n "Check '$CMD'"
    $CMD && echo " --> OK" || { echo " --> ERROR: $MSG_ERR"; exit 1; }
}

test_first() {

    echo
    echo_bold "Testing file permissions, PostgreSQL version and installed commands/services"
    echo

    PGVERSION=$(psql_call -d $PGDBDEFAULT -tc "SHOW server_version_num")
    V=$(psql_call -d $PGDBDEFAULT -tc "SHOW server_version")
    if (( $PGVERSION < 90500 )); then
        echo "ERROR: PostgreSQL must be at least version 9.5, but installed version is $V."
        echo "Terminating..."
        exit 1
    else
        echo "SUCCESS: PostgreSQL version:  $V"
    fi

    test_installation "missing... please install it" which sudo
    test_installation "missing... please install it" which mvn
    test_installation "missing... please install it" which psql
    test_installation "missing... please install it" which xmlstarlet

    echo "Check Java Version == $JAVA_VERSION... "
    (java -version 2>&1 | grep "$JAVA_VERSION") > /dev/null && {
        echo " --> OK"
    } || {
        echo " --> ERROR: Please install it, or use another Java version at your own risk"
        exit 1
    }

    test_installation "File $PXMLFILE not readable." test -r $PXMLFILE.dist
    PXMLDIR=$(dirname $PXMLFILE)
    test_installation "Folder $PXMLDIR not writeable." test -w $PXMLDIR
    test_installation "File $MODSSQL not readable." test -r $MODSSQL
    test_installation "File $DUMPSQL not readable." test -r $DUMPSQL
    test_installation "/usr/sbin/service not executable." test -x /usr/sbin/service
    test_installation "Service $APPSERVER not running... install and/or start it." /usr/sbin/service $APPSERVER status | grep running

    echo_bold "...DONE."
}

# execute script
give_consent

if [ "$answer" == "yes" ]; then
    test_first
    db_setup_db_and_users
    db_setup_import_sqlfiles

    # Change the owner of all tables inside $PGSCHEMA
    for table in $(psql_call -d $PGDBNAME -tc "SELECT tablename FROM pg_tables WHERE schemaname = '${PGSCHEMA}'"); do
        psql_call -d $PGDBNAME -c "ALTER TABLE ${PGSCHEMA}.${table} OWNER TO ${PGUSER1}"
    done

    db_setup_privileges

    create_log_files
    update_persistence
    deploy_war_files
    final_message
else
    echo_bold "BDP-core not installed."
    echo "Please remember to change the variables in the script to suit your needs!"
    tput sgr0
    exit 127
fi

tput sgr0
exit 0
