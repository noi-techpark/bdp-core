# Big Data Platform

This platform was developed to collect heterogeneous data of different sources and different types, do elaborations on it and serve the raw and elaborated data through a REST interface.
## Table of contents
1. [CORE](##core)
  1. [DAL (data access layer)](###dal)
    1. [Entity Structure](###entity-structure)
  2. [DTO (data transfer object)](###dto)
  3. [WRITER](###writer)
    1. [dc-interface](###dc-interface)
  4. [READER](###reader)
    1. [ws-interface](###ws-interface)

## CORE

The core of the platform contains all the business logic which handles connections with the database, with the data collectors which provide the data and with the webservices which serve the data.

### DAL

Is the layer which communicates with the db underneath used by the so called reader and writer modules. The communication is handled through the ORM Hibernate and its spatial component for geometrys. The whole module got developed with postgresql as database and postgis as an extension, but should also work with other rdbms (nobody knows :)).

Connection pooling is handled by [HikariCP](https://github.com/idm-suedtirol/bdp-core) for high speed connections to the db.

In some cases geometry transformations and elaborations where needed to be executed on application level and therefore [Geotools](http://www.geotools.org/) was   added as dependency.

To configure the DAL module to communicate with your database you need to provide configuration and credentials:
>`src/main/resources/META-INF/persistence.xml`

As you will see 2 persistence-units are configured. One is meant for the reader with preferably a read-only user and the other one for the writer which performs all the transactions.
You can either import the schema

  `insert schema command here`

or let the ORM automaticaly create it for you by replacing
> hibernate.hbm2ddl.autohibernate.hbm2ddl.auto `validate` to `update`

#### Entity structure
The core strutcture of the bdp is quiet simple. There exists 3 entities on which all relies on
  - **Station** : Represents the origin of the data which only needs an idenfier, a coordinate and a so called stationtype. Additional fields are the provider of the data, a name and description.

  *Example*: station can be of stationtype `MeteoStation`, have a identifier `89935GW` and a position `latitude":46.24339407235059,"longitude":11.199431152658656`
  - **DataType**: Represents the typology of the data in form of a unique name and a unit. Description and type of measurement can also be provided.

  *Example*: temperature can have a unit `°C` and can be an `average value` of the last 5 minutes.
  - **Record** : Represents a single data entry containing a Value a Timestamp a DataType, a Station and a survey time frame in seconds.

  *Example*: the value is `20.4` the timestamp is `Fri Dec 16 2016 10:47:33`, the type is the one we just created before and the same for station. The time frame of the survey could be 1 hour and therefore `3600`

#### More about entities to come ...

## DTO
Data transfer objects are used as format for exchanging the data. They are used between data provider and data persister(writer) but also between data dispatcher and data reader (reader). This dto module is a java library contained in all modules of the big data platform, simply because it contains the structure of communication between the modules.
- dtos between data collectors and writer:
- dtos between webservices and reader:

## WRITER
The writer is a simple interface exposing calls through a xmlrpc service.
It integrates the DAL and saves the data through abstraction.
The calls which the service exposes can be found out through introspection and the most important are:
  - Object syncStations(String stationType, Object...data):
    syncronizes data of stations with the given stationType
  - Object syncDataTypes(Object...data):
    syncronizes data types
  - Object pushRecords(String stationType, Object... data)
    persist new data of stations with specific station types

### dc-interface
The writer comes with a ready to go java implementation. Just include the dc-interface jar in your project and use the existing xmlrpc client implementation.

More informations will be available soon.

## READER
The reader is a simple interface exposing calls through a xmlrpc service.
It integrates the DAL and retrieves the data through abstraction.

### ws-interface
Luckily on the reader side there exists already a Java implementation for the API to get the data you need. To be able to use it you need to include the ws-interface jar file in your dependencies and than use the xmlrpc-client implementation. If you want to serve the data as json throught the provided API you can also use the spring web-mvc Rest implementation with jwt authentication.

More informations will be available soon.
