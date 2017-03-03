# Big Data Platform

This platform was developed to collect heterogeneous data of different sources and different types, do elaborations on it and serve the raw and elaborated data through a REST interface.
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
- dtos between data collectors and writer:
- dtos between webservices and reader:

## WRITER
The writer is a simple interface exposing calls through a xmlrpc service.
The DAL is a big and shared part between writer and reader and saves and retrieves the data through abstraction.
The writer himself implements the methods to write data to the bdp and therefore becomes an interface for all data collectors.


### dc-interface
The dc-interface contains the API through which components can comunicate with the bdp writer. Just include the dc-interface jar-file in your project and use the existing xmlrpc client implementation.
The API is compact and easy to use:
	
  - `Object syncStations(String dataStationTypology, Object[] data)` : This method is used to create,update,delete stations of a specific typology data must be an array of StationDto's and depending on dataStationTypology it can also be an extension of it. 
  If, for example dataStationTypology = "Meteostation", the array can contain StationDto but also MeteostationDto
	
  - `Object syncDataTypes(String dataStationTypology, Object[] data)` : This method is used to create and update data types. Data must be an array of DataTypeDto
  
  - `Object pushData(String datasourceName, Object[] data)` : Here comes the most important one. This is the place where you place all the data you want to pass to the bdp. You have to put everything in a map which has as key a string identifying the data station it is correlated and a TypeMapDto as value. TypeMapDto himself contains a map himself where value is a unique string identifier representing the datatype the data is correlated to. As value you can put a list of SimpleRecordDto which simply are all the datapoints with a specific staiton and  a specific type. Each point is represented as timestamp and value
  
  - `Object getDateOfLastRecord(String stationCode,String dataType,Integer period)` : this method is required to get the date of the last valid record

**datasourceName** identifies which kind of data needs to be saved

## READER
The reader is a simple interface exposing calls through a xmlrpc service.
It integrates the DAL and retrieves the data through abstraction.

### ws-interface
Luckily on the reader side there exists already a Java implementation for the API to get the data you need. To be able to use it you need to include the ws-interface jar file in your dependencies and than use the xmlrpc-client implementation. If you want to serve the data as json throught the provided API you can also use the spring web-mvc Rest implementation with jwt authentication.

More informations will be available soon.

## Installation guide
### Prerequisits
- postgresql 9.3 or higher with postgis 2.2 extension
- application server (we are using tomcat8)
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
  psql bd < schema.dump

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
To check the endpoints of the 2 apps go to http://{host}:{port}/writer/xmlrpc and http://{host}:{port}/reader/xmlrpc. There you should get 405 method GET not allowed as response. You can test it by seeing if introspection works:

  `curl -i --data @introspection.xml http://localhost:8080/writer/xmlrpc`

  The response should be somehing like this:
  ``` xml
  <?xml version="1.0" encoding="UTF-8"?>
  <methodResponse xmlns:ex="http://ws.apache.org/xmlrpc/namespaces/extensions">
    <params>
      <param>
        <value>
          <array>
            <data>
              <value>system.methodSignature</value>
              <value>DataCollector.getLatestMeasurementStringRecord</value>
              <value>system.methodHelp</value>
              <value>DataCollector.syncStations</value>
              <value>DataCollector.syncDataTypes</value>
              <value>DataCollector.getDateOfLastRecord</value>
              <value>DataCollector.pushRecords</value>
              <value>system.listMethods</value>
            </data>
          </array>
        </value>
      </param>
    </params>
  </methodResponse>
  ```

  The same goes for the reader application:

  `curl -i --data @introspection.xml http://localhost:8080/writer/xmlrpc`
  ``` xml
  <?xml version="1.0" encoding="UTF-8"?>
  <methodResponse xmlns:ex="http://ws.apache.org/xmlrpc/namespaces/extensions">
    <params>
      <param>
        <value>
          <array>
            <data>
              <value>DataRetriever.getDataTypes</value>
              <value>DataRetriever.getTypes</value>
              <value>DataRetriever.getStationDetails</value>
              <value>DataRetriever.getStations</value>
              <value>system.methodHelp</value>
              <value>DataRetriever.getFreeSlotsByTimeFrame</value>
              <value>DataRetriever.getRecords</value>
              <value>DataRetriever.getNumberOfFreeSlots</value>
              <value>system.listMethods</value>
              <value>DataRetriever.getAvailableStations</value>
              <value>system.methodSignature</value>
              <value>DataRetriever.getChildren</value>
              <value>DataRetriever.getLastRecord</value>
              <value>DataRetriever.getParkingStations</value>
              <value>DataRetriever.getParkingIds</value>
              <value>DataRetriever.getNewestRecord</value>
              <value>DataRetriever.getStoricData</value>
              <value>DataRetriever.getParkingStation</value>
              <value>DataRetriever.getDateOfLastRecord</value>
            </data>
          </array>
        </value>
      </param>
    </params>
  </methodResponse>
  ```

If this works you made it.

You deployed your bd-core and now will be able to add modules or develop your own.
For more informations read the modules manual.
