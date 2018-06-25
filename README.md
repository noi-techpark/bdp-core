# Big Data Platform

This platform collects heterogeneous data of different sources and different
types, does elaborations on it and serves the raw and elaborated data through
a REST interface.

The Big Data Platform Core is free software. It is licensed under GNU GENERAL
PUBLIC LICENSE Version 3 from 29 June 2007 (see LICENSE/GPLv3).


## Table of contents
1. [CORE](#core)
  1. [DAL (data access layer)](#dal)
    1. [Entity Structure](#entity-structure)
  2. [DTO (data transfer object)](#dto)
  3. [Writer (data persister)](#writer)
    1. [dc-interface](#dc-interface)
  4. [Reader (data dispatcher)](#reader)
    1. [ws-interface](#ws-interface)
  5. [Installation guide](#installation-guide)
  6. [License updates](#license-updates)

## CORE

The core of the platform contains all the business logic which handles connections with the database, with the data collectors which provide the data and with the webservices which serve the data.

### DAL

Is the layer which communicates with the db underneath used by the so called reader and writer modules. The communication is handled through the ORM Hibernate and its spatial component for geometrys. The whole module got developed with postgresql as database and postgis as an extension, but should also work with other rdbms (nobody knows :)).

Connection pooling is handled by [HikariCP](https://github.com/idm-suedtirol/bdp-core) for high speed connections to the db.

In some cases geometry transformations and elaborations where needed to be executed on application level and therefore [Geotools](http://www.geotools.org/) was added as dependency.

To configure the DAL module to communicate with your database you need to provide configuration and credentials:
>`src/main/resources/META-INF/persistence.xml`

As you will see 2 persistence-units are configured. One is meant for the reader with preferably a read-only user and the other one for the writer which performs all the transactions.
You can either import the schema

  `psql [database] < schema.dump`

or let the ORM automaticaly create it for you by replacing
> hibernate.hbm2ddl.autohibernate.hbm2ddl.auto `validate` to `update`

#### Entity structure
The core strutcture of the bdp is quiet simple. There exists 3 entities on which all relies on
  - **Station** : Represents the origin of the data which only needs an idenfier, a coordinate and a so called stationtype. Additional fields are the provider of the data, a name and description.

  *Example*: station can be of stationtype `MeteoStation`, have a identifier `89935GW` and a position `latitude":46.24339407235059,"longitude":11.199431152658656`
  - **DataType**: Represents the typology of the data in form of a unique name and a unit. Description and type of measurement can also be provided.

  *Example*: temperature can have a unit `°C` and can be an `average` value of the last 5 minutes.
  - **Record** : Represents a single data entry containing a Value a Timestamp a DataType, a Station and a survey time frame in seconds.

  *Example*: the value is `20.4` the timestamp is `Fri Dec 16 2016 10:47:33`, the type is the one we just created before and the same for station. The time frame of the survey could be 1 hour and therefore `3600`

#### More about entities to come ...

## DTO
Data transfer objects are used as format for exchanging the data. They are used between data provider and data persister(writer) but also between data dispatcher and data reader (reader). This dto module is a java library contained in all modules of the big data platform, simply because it contains the structure of communication between the modules.
- dtos between data collectors and writer: //TODO add definitive dtos
- dtos between webservices and reader: //TODO add definitive dtos

## WRITER
The writer is a simple interface exposing rest endpoints, where data can be pushed as json in the structures defined in dto.
The DAL is a big and shared part between writer and reader and saves and retrieves the data through abstraction.
The writer himself implements the methods to write data to the bdp and is therefore the endpoint for all datacollectors.
It uses the persistence-unit of the DAL which has permissions to read all data and also to write everything.


### dc-interface
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

## READER
The reader is a simple interface exposing calls through a json service (RestClient).
It depends on the DAL and retrieves the data through abstraction.
It uses the persistence unit which has read only access on the db.

### ws-interface
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
  - import the database schema
  ```
  createdb bd
  createuser bd
  createuser bdreadonly
  psql bd
  > alter database bd owner to bd
  > GRANT SELECT ON ALL TABLES IN SCHEMA intime TO bdreadonly;
  > GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA intime TO bdreadonly;
  > create extension postgis
  psql -U bd bd < schema.dump
```

### Step2: Configure and deploy your big data platform
  - clone git repo bdp-core
  - Install the dto module
  - Setup and install your dal module
  - Compile and package your writer and reader module
  - Deploy the generated writer.war and reader.war on your application server

```
  git clone https://github.com/idm-suedtirol/bdp-core.git
  cd bdp-core
  cd dto
  mvn install
  cd dal
  vim src/main/resources/META-INF/persistence.xml
  mvn clean install
  cd ../writer
  mvn clean package
  cd../reader
  mvn clean package
```

### Step 3: Check endpoints
To check the endpoints of the 2 apps go to `http://{host}:{port}/writer/json`
and `http://{host}:{port}/reader/json`. There you should get
`405 method GET not allowed` as response.

If this works you made it.

You deployed your bd-core and now will be able to add modules or develop your own.
For more informations read the modules manual.


## Licenses

### Source file updates
To update license headers in each source code file run `mvn license:format`.
To configure the header template edit `LICENSE/templates/` files, and
set the correct attributes inside each `pom.xml`. See the plugin
[license-maven-plugin](http://code.mycila.com/license-maven-plugin/)
homepage for details.

### Details of this project
Run `mvn site` to create a HTML page with all details of this project.
Results can be found under `<project>/target/site/`, entrypoint is as
usual `index.html`.
