# Big Data Platform

The Big Data Platform is part of the [Open Data Hub](http://opendatahub.bz.it/) project.
It collects and exposes mobility data sources.

This platform collects heterogeneous data of different sources and different
types, does elaborations on it and serves the raw and elaborated data through
a REST interface.

The Big Data Platform Core is free software. It is licensed under GNU GENERAL
PUBLIC LICENSE Version 3 from 29 June 2007 (see LICENSE file).

[![reuse compliant](https://reuse.software/badge/reuse-compliant.svg)](https://reuse.software/)

----

#### Table of Contents
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [CORE](#core)
  - [DAL](#dal)
    - [Entity structure](#entity-structure)
    - [More about entities to come ...](#more-about-entities-to-come-)
  - [DTO](#dto)
  - [WRITER](#writer)
    - [dc-interface](#dc-interface)
  - [READER](#reader)
    - [ws-interface](#ws-interface)
- [Installation guide](#installation-guide)
  - [Prerequisits](#prerequisits)
  - [Step1: Set up your database](#step1-set-up-your-database)
  - [Step2: Configure and deploy your big data platform](#step2-configure-and-deploy-your-big-data-platform)
  - [Step 3: Check endpoints](#step-3-check-endpoints)
- [Licenses](#licenses)
  - [I want to update license headers of each source file](#i-want-to-update-license-headers-of-each-source-file)
  - [I want to see details of this project as HTML page](#i-want-to-see-details-of-this-project-as-html-page)
  - [I want to update the CONTRIBUTORS.rst file](#i-want-to-update-the-contributorsrst-file)
  - [Third party components](#third-party-components)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

----

## CORE

The core of the platform contains all the business logic which handles connections with the database, with the data collectors which provide the data and with the webservices which serve the data.

### DAL

Is the layer which communicates with the db underneath used by the so called reader and writer modules. The communication is handled through the ORM Hibernate and its spatial component for geometrys. The whole module got developed with postgresql as database and postgis as an extension, but should also work with other rdbms (nobody knows :)).

Connection pooling is handled by [HikariCP](https://github.com/idm-suedtirol/bdp-core) for high speed connections to the db.

In some cases geometry transformations and elaborations where needed to be executed on application level and therefore [Geotools](http://www.geotools.org/) was added as dependency.

To configure the DAL module to communicate with your database you need to provide configuration and credentials:
>`src/main/resources/META-INF/persistence.xml`

As you will see 2 persistence-units are configured. One is meant for the reader with preferably a read-only user and the other one for the writer which performs all the transactions.

Let the ORM handle the schema creation which will create all tables, views, sequences, primary keys, foreign keys, constraints etc.  automatically. Do so by changing the property
> hibernate.hbm2ddl.auto `validate` to `update`

#### Entity structure
The core strutcture of the bdp is quiet simple. There exists 3 entities on which all relies on
  - **Station** : Represents the origin of the data which only needs an idenfier, a coordinate and a so called stationtype. Additional fields are the provider of the data, a name and description.

	Station is an abstract class containing generic operations and fields, valid for all type of stations. **MeasurementStation** extends Station and contains the business logic on how DataRecords which are measurements get stored to the database through the entities **Measurement** and **MeasurementHistory**. Measurements are identified by a timestamp, a double precision value, a reference to the type and a reference to the station.
	**ElaborationStation** works quite similar but handles data which are elaborations created with data already contained inside the bigdataplatform. **Elaboration** and **ElaborationHistory** get used to store this data and are quite similar to Measurement and MeasurementHistory.
	ElaborationStation and MeasurementStation are abstract classes which get extended by different classes containing the identifier of a stationtype.

	Sometimes incoming data data might be not numbers, but Strings. These kind of data is stored throught the entities **MeasurementString** and **MeasurementStringHistory**.

  *Example*: station can be of stationtype **MeteoStation**, have a identifier `89935GW` and a position `latitude":46.24339407235059,"longitude":11.199431152658656`
	It extends MeasurementStation and therefore needs close to no additional implementation to store data.
  - **DataType**: Represents the typology of the data in form of a unique name and a unit. Description and type of measurement can also be provided.

  *Example*: temperature can have a unit `°C` and can be an `average` value of the last 5 minutes.
  - **Record** : Represents a single data entry containing a Value a Timestamp a DataType, a Station and a survey time frame in seconds.

  *Example*: the value is `20.4` the timestamp is `Fri Dec 16 2016 10:47:33`, the type is the one we just created before and the same for station. The time frame of the survey could be 1 hour and therefore `3600`

#### More about entities to come ...

### DTO
Data transfer objects are used as format for exchanging the data. They are used between data provider and data persister(writer) but also between data dispatcher and data reader (reader). This dto module is a java library contained in all modules of the big data platform, simply because it contains the structure of communication between the modules.
- dtos between data collectors and writer: //TODO add definitive dtos
- dtos between webservices and reader: //TODO add definitive dtos

### WRITER
The writer is a simple interface exposing rest endpoints, where data can be pushed as json in the structures defined in dto.
The DAL is a big and shared part between writer and reader and saves and retrieves the data through abstraction.
The writer himself implements the methods to write data to the bdp and is therefore the endpoint for all datacollectors.
It uses the persistence-unit of the DAL which has permissions to read all data and also to write everything.


#### dc-interface
The dc-interface contains the API through which components can comunicate with the bdp writer. Just include the dc-interface jar-file in your project and use the existing json client implementation(JSONPusher.java).
The API is compact and easy to use:

  - `Object syncStations(String dataStationTypology, StationList data)` : This method is used to create,update,delete stations of a specific typology data must be a StationList(List of StationDto's)

  If, for example dataStationTypology = "Meteostation", the array can contain StationDto but also MeteostationDto

  - `Object syncDataTypes(String dataStationTypology, List<DataTypeDto> data)` : This method is used to create and update data types. Data must be a list of DataTypeDto

  - `Object pushData(String datasourceName,  DataMapDto<? extends RecordDtoImpl> dto)` : Here comes the most important one. This is the place where you place all the data you want to pass to the bdp. The data in here gets saved in form of a tree.
Each branch can have multiple child branches but can also have data itself, which means it can have indefinitive depth.
Right now, by our internal conventions we store everything on the second level, like this:

```
----Station

-----------DataType

--------------------Data
```
  As value you can put a list of SimpleRecordDto which simply are all the datapoints with a specific station and  a specific type. Each point is represented as timestamp and value. To better understand the structure, watch the source:
https://github.com/idm-suedtirol/bdp-core/blob/master/dto/src/main/java/it/bz/idm/bdp/dto/DataMapDto.java
  
  - `Object getDateOfLastRecord(String stationCode,String dataType,Integer period)` : this method is required to get the date of the last valid record

**datasourceName** identifies which kind of data needs to be saved

### READER
The reader is a simple interface exposing calls through a json service (RestClient).
It depends on the DAL and retrieves the data through abstraction.
It uses the persistence unit which has read only access on the db.

#### ws-interface
Luckily on the reader side there exists already a Java implementation for the API to get the data you need. To be able to use it you need to include the ws-interface jar file in your dependencies and than use the RestClient implementation. If you want to serve the data as json throught the provided API you can also use the spring web-mvc Rest implementation with jwt authentication.

More informations will be available soon.

## Installation guide
### Prerequisits
- postgresql 9.3 or higher with postgis 2.2 extension
- application server (we use tomcat8)
- JRE7+ (we use JRE8)

### Step1: Set up your database
  - create a database and add postgis extension
  - create a user with full permissions on the db
  - create a user with read only permissions on the db


```
  createdb bd
  createuser bd
  createuser bdreadonly
  psql bd
  > alter database bd owner to bd;
	> create schema intime authorization bd;
  > GRANT SELECT ON ALL TABLES IN SCHEMA intime TO bdreadonly;
  > GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA intime TO bdreadonly;
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
  cd ../dal
  vim src/main/resources/META-INF/persistence.xml
	> <property name="hibernate.hbm2ddl.auto" value="create"/>
	> <property name="hibernate.hikari.dataSource.databaseName" value="bd"/>
	> <property name="hibernate.hikari.dataSource.user" value="bd"/>
	> <property name="hibernate.hikari.dataSource.password" value=""/>
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
To check the endpoints of the 2 apps go to `http://{host}:{port}/writer/json/stationsWithoutMunicipality`. Here you should get an empty array, if no stations exist yet.
and `http://{host}:{port}/reader/stations`. There you should get
`400 BadRequest for missing parameter stationType` as response.

If this works you made it.

You deployed your bdp-core and now will be able to add modules or develop your own.
If you want to fill your db with data, you will either create your own module, which works with the writer API or you can follow the guide on https://github.com/idm-suedtirol/bdp-helloworld/tree/master/data-collectors/my-first-data-collector where it's shown hot to use an already existing JAVA-client.
If you also need to expose this data you can either use the reader API or use the existing client interface ws-interface.

For more informations read the modules manual.


## Licenses

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

### Third party components

- `CONTRIBUTORS.rst` script done by [Daniele Gobbetti](https://github.com/danielegobbetti)
