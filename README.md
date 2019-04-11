# Big Data Platform

The Big Data Platform is part of the [Open Data Hub](http://opendatahub.bz.it/) project.
It collects and exposes data sets of various domains.

This platform collects heterogeneous data of different sources and different domains, does elaborations on it and serves the raw and elaborated data through a REST interface.

The Big Data Platform Core is free software. It is licensed under GNU GENERAL
PUBLIC LICENSE Version 3 from 29 June 2007 (see `LICENSE` file).

[![reuse compliant](https://reuse.software/badge/reuse-compliant.svg)](https://reuse.software/)

----

#### Table of Contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [CORE](#core)
  - [DAL](#dal)
    - [Station](#station)
    - [DataType](#datatype)
    - [Record](#record)
    - [Edge](#edge)
  - [DTO](#dto)
    - [StationDto](#stationdto)
    - [DataTypeDto](#datatypedto)
    - [SimpleRecordDto](#simplerecorddto)
  - [WRITER](#writer)
    - [dc-interface](#dc-interface)
  - [READER](#reader)
    - [Authentication](#authentication)
    - [Authorization](#authorization)
    - [ws-interface](#ws-interface)
- [Installation guide](#installation-guide)
  - [Prerequisits](#prerequisits)
  - [Step1: Set up your database](#step1-set-up-your-database)
  - [Step2: Configure and deploy your big data platform](#step2-configure-and-deploy-your-big-data-platform)
  - [Step 3: Check endpoints](#step-3-check-endpoints)
- [Flight rules](#flight-rules)
  - [I want to generate a new schema dump out of Hibernate's Entity classes](#i-want-to-generate-a-new-schema-dump-out-of-hibernates-entity-classes)
  - [I want to update license headers of each source file](#i-want-to-update-license-headers-of-each-source-file)
  - [I want to see details of this project as HTML page](#i-want-to-see-details-of-this-project-as-html-page)
  - [I want to update the CONTRIBUTORS.rst file](#i-want-to-update-the-contributorsrst-file)
- [Licenses](#licenses)
  - [Third party components](#third-party-components)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

----

## CORE

The core of the platform contains all the business logic which handles connections with the database, with the data collectors which provide the data and with the web services which serve the data.

### DAL

Is the layer which communicates with the db underneath used by the so called reader and writer modules. The communication is handled through the ORM Hibernate and its spatial component for geometries. The whole module got developed with PostgreSQL as database and Postgis as an extension, but should also work with other RDBMS (nobody knows :)).

Connection pooling is handled by [HikariCP](https://github.com/idm-suedtirol/bdp-core) for high speed connections to the db.

In some cases geometry transformations and elaborations where needed to be executed on application level and therefore [Geotools](http://www.geotools.org/) was added as dependency.

To configure the DAL module to communicate with your database you need to provide configuration and credentials:
>`src/main/resources/META-INF/persistence.xml`

As you will see 2 persistence-units are configured. One is meant for the reader with preferably a read-only user and the other one for the writer which performs all the transactions.

Let the ORM handle the schema creation which will create all tables, views, sequences, primary keys, foreign keys, constraints etc.  automatically. Do so by changing the property
> hibernate.hbm2ddl.auto `validate` to `update`

The core strutcture of the bdp is quiet simple. There exists 4 entities on which all relies on
#### Station
  represents the origin of the data which needs an identifier, a name, a coordinate and a so called `stationtype`. It also should contain the origin of the data, the current *active* state(if actively used or not) and if it has another station which it regards as parent it will need a reference to it. For all remaining data, which enriches the station, we created a field *metadata* which can hold any kind of meta information in form of a JSON object. To understand the functionality and the main job of this entity check the source code https://github.com/idm-suedtirol/bdp-core/blob/master/dal/src/main/java/it/bz/idm/bdp/dal/Station.java

 	*Example*:
	>station can be of stationtype `MeteoStation`, have a identifier `89935GW` and a position `latitude":46.24339407235059,"longitude":11.199431152658656`.
	It can have additional information like address, municipality, opening times etc.


#### DataType
   represents the typology of the data in form of a unique namem and a unit. Description and metric of measurements can also be provided.

  *Example*:
  >temperature can have a unit `°C` and can be an `average` value of the last 5 minutes.

#### Record
  represents a single data entry containing a value a timestamp a data-type, a station, a provenance and a survey time frame in seconds. Provenance is the data collector identifier which the data comes from and is just a reference to the entity `Provenance`.

  *Example*:
  > the value is `20.4` the timestamp is `Fri Dec 16 2016 10:47:33`, the type is the one we just created before and the same for station. The time frame of the survey could be 1 hour and therefore `3600`. Provenance will just be an id like `10013`, referencing another entity.

#### Edge
  an edge represents the spatial geometry between 2 or more stations.  This was modeled as station in v1 but is getting extracted to have a more dynamic way to handle special cases.

  *Example*:
  > It could be a street where the measured data could be how many cars passed between these 2 stations.


If you need more information about specific entities or classes, try to use the javadoc: https://github.com/noi-techpark/bdp-core/tree/master/dal

### DTO
Data transfer objects are used to define the structure of the data exchange. They are used between data provider and data persister(writer) but also between data dispatcher and data reader (reader). They consist of fields which are all primitives and easily serializable.
The dto module is a java library contained in all modules of the big data platform, simply because it defines the communication structure in between.
https://github.com/noi-techpark/bdp-core/tree/master/dto

Most used DTO's you will stumble upon:

#### StationDto
Describes the point of origin of the data.

https://github.com/noi-techpark/bdp-core/blob/master/dto/src/main/java/it/bz/idm/bdp/dto/StationDto.java

#### DataTypeDto
Describes a specific type of data.

https://github.com/noi-techpark/bdp-core/blob/master/dto/src/main/java/it/bz/idm/bdp/dto/DataTypeDto.java

#### SimpleRecordDto
Describes the measured value.

https://github.com/noi-techpark/bdp-core/blob/master/dto/src/main/java/it/bz/idm/bdp/dto/SimpleRecordDto.java

### WRITER
The writer is a simple API exposing rest endpoints, where data can be pushed as json in the structures defined in dto.
The DAL is a big and shared part between writer and reader, which saves and retrieves the data through abstraction.
The writer himself implements the methods to write data to the bdp and is therefore the endpoint for all data collectors.
It uses the persistence-unit of the DAL which has full permissions on the database.


#### dc-interface
The dc-interface contains the API through which components can communicate with the bdp writer. Just include the dc-interface jar-file in your project and use the existing json client implementation(https://github.com/noi-techpark/bdp-core/blob/master/dc-interface/src/main/java/it/bz/idm/bdp/json/JSONPusher.java).
The API is quiet slim and self explaining:

  - `Object syncStations(List<StationDto> data)` : This method is used to create,update,deactivate stations of a specific typology data must be a list of StationDto's

  - `Object syncDataTypes(List<DataTypeDto> data)` : This method is used to create and update(and therefore upsert) data types. Data must be a list of DataTypeDto

  - `Object pushData(DataMapDto<? extends RecordDtoImpl> dto)` : Here comes the most important one. This is the place where you place all the data you want to pass to the bdp. The data in here gets saved in form of a tree.

Each branch can have multiple child branches but can also have data itself, which means it can have indefinite depth.
Right now, by our internal conventions we store everything on the second level, like this:

```
----Station

-----------DataType

--------------------Data
```
  As value you can put a list of SimpleRecordDto which simply are all the data points with a specific station and  a specific type. Each point is represented as timestamp and value. To better understand the structure, watch the source:
https://github.com/idm-suedtirol/bdp-core/blob/master/dto/src/main/java/it/bz/idm/bdp/dto/DataMapDto.java
  
  - `Object getDateOfLastRecord(String stationCode,String dataType,Integer period)` : this method is required to get the date of the last valid record

### READER
The reader is a simple API exposing data through a json service (https://github.com/noi-techpark/bdp-core/blob/master/ws-interface/src/main/java/it/bz/idm/bdp/ws/RestClient.java).
It depends on the DAL and retrieves the data by querying the underlying database through JPA query language.
#### authentication
The reader side handles authentication for non-opendata through an OAuth authentication mechanism using [JWT](https://jwt.io/),  https://github.com/jwtk/jjwt. To get an account to retrieve specific data you can't access, write us a request to info@opendatahub.bz.it.

#### authorization
Each user we create is associated to a *ROLE*, which has a set of permissions. The rules which define this permissions are currently inserted manually but will be replaced by an ACL-system(work in progress). Currently there are 2 default roles:
- *ADMIN* ... has full access on all existing data in the database
- *GUEST* ... has restricted access to all data published under an open data license we currently provide. Everybody who is not authenticated is therefore automatically using this role to access data.

#### ws-interface
Luckily on the reader side there exists already a Java implementation for the API to get the data you need. To be able to use it you need to include the ws-interface jar file in your dependencies and than use the RestClient implementation. If you want to serve the data as json through the provided API you can also use the spring web-mvc Rest implementation with jwt authentication.

More information will be available soon.

## Installation guide

The following section is partially deprecated. Please use the `quickbuild.sh` script to setup your environment. Usage information can be found inside the script. The configuration of the script must be made inside the script itself. The `SQLVERSION` variable specifies the dump and modification SQL script versions inside DAL.

Example:
```
    PGUSER=postgres PGPASSWORD=yourpassword SQLVERSION=1.3.6 ./quickbuild.sh
```


### Prerequisits
- postgresql 9.5 or higher with postgis extension
- application server (we use tomcat8)
- Java Runtime Environment 8

### Step1: Set up your database
  - create a database and add postgis extension
  - create a user with full permissions on the db
  - create a user with read only permissions on the db


```
  createdb odh
  createuser odhAdmin
  createuser odhReadonly
  psql odh
  > alter database odh owner to odhAdmin;
	> create schema intime authorization odhAmdin;
  > GRANT SELECT ON ALL TABLES IN SCHEMA intime TO odhReadonly;
  > GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA intime TO odhReadonly;
  > create extension postgis;
```

### Step2: Configure and deploy your big data platform
  - clone git repo bdp-core
  - Install the dto module
	- configure persistence layer and install dal
	- deploy and start the writer module
	- trigger the schema auto creation
	- reconfigure your persistence.xml to avoid drop of data
	- deploy and start the reader module

```
  git clone https://github.com/idm-suedtirol/bdp-core.git
  cd bdp-core/dto
  mvn install
  cd ../dal/src/main/resources/META-INF/persistence.xml
  cp persistence.xml.dist persistence.xml
  vim persistence.xml
	> <property name="hibernate.hbm2ddl.auto" value="create"/>
	> <property name="hibernate.hikari.dataSource.databaseName" value="bd"/>
	> <property name="hibernate.hikari.dataSource.user" value="bd"/>
	> <property name="hibernate.hikari.dataSource.password" value=""/>
  cd ../../../..
  mvn clean install
  cd ../writer
  mvn clean package
	curl -i 127.0.0.1:8080/writer/json/stationsWithoutMunicipality
	vim src/main/resources/META-INF/persistence.xml
	> <property name="hibernate.hbm2ddl.auto" value="validate"/>
  cd ../writer
  mvn clean package
  cd../reader
  mvn clean package
```

### Step 3: Check endpoints
To check the endpoints of the 2 apps go to `http://{host}:{port}/writer/json/stations`. Here you should get an empty array, if no stations exist yet.
and `http://{host}:{port}/reader/stations`. There you should get
`400 BadRequest for missing parameter stationType` as response.

If this works you made it.

You deployed your bdp-core and now will be able to add modules or develop your own.
If you want to fill your db with data, you will either create your own module, which works with the writer API or you can follow the guide on https://github.com/idm-suedtirol/bdp-helloworld/tree/master/data-collectors/my-first-data-collector where it's shown hot to use an already existing JAVA-client.
If you also need to expose this data you can either use the reader API or use the existing client interface ws-interface.

For more informations read the modules manual.

## Flight rules

### I want to generate a new schema dump out of Hibernate's Entity classes

See [README.md](https://github.com/noi-techpark/bdp-core/blob/master/tools/README.md) inside `/tools`.

### I want to update license headers of each source file
To update license headers in each source code file run `mvn license:format`.
To configure the header template edit `LICENSE/templates/` files, and
set the correct attributes inside each `pom.xml`. See the plugin
[license-maven-plugin](http://code.mycila.com/license-maven-plugin/)
homepage for details. Use the `quicklicense.sh` script to update all
source code license headers at once.

### I want to see details of this project as HTML page
Run `mvn site` to create a HTML page with all details of this project.
Results can be found under `<project>/target/site/`, entrypoint is as
usual `index.html`.

### I want to update the CONTRIBUTORS.rst file
Just run `bash CONTRIBUTORS.rst` and check the output inside the file
itself. Configure any mail or name mappings inside `.mailmap`. See
`man git shortlog` for further details.

## Licenses



### Third party components

- `CONTRIBUTORS.rst` script done by [Daniele Gobbetti](https://github.com/danielegobbetti)
