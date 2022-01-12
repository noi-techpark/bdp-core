# Big Data Platform

The Big Data Platform is part of the [Open Data Hub](http://opendatahub.bz.it/)
project. It collects and exposes data sets of various domains.

This platform collects heterogeneous data of different sources and different
domains, does elaborations on it and serves the raw and elaborated data through
a REST interface.

For a detailed introduction, see our [Big Data Platform
Introduction](https://opendatahub.readthedocs.io/en/latest/intro.html).

The Big Data Platform Core is free software. It is licensed under GNU GENERAL
PUBLIC LICENSE Version 3 from 29 June 2007 (see `LICENSE` file).

----

**Table of Contents**

- [Big Data Platform](#big-data-platform)
  - [CORE](#core)
    - [WRITER](#writer)
      - [Authentication](#authentication)
      - [dc-interface](#dc-interface)
    - [READER](#reader)
      - [Authentication](#authentication-1)
      - [Authorization](#authorization)
      - [ws-interface](#ws-interface)
    - [DAL](#dal)
      - [Station](#station)
      - [DataType](#datatype)
      - [Record](#record)
      - [Edge](#edge)
    - [DTO](#dto)
      - [StationDto](#stationdto)
      - [DataTypeDto](#datatypedto)
      - [SimpleRecordDto](#simplerecorddto)
  - [Installation guide](#installation-guide)
  - [Flight rules](#flight-rules)
    - [I want to generate a new schema dump out of Hibernate's Entity classes](#i-want-to-generate-a-new-schema-dump-out-of-hibernates-entity-classes)
    - [I want to update license headers of each source file](#i-want-to-update-license-headers-of-each-source-file)
    - [I want to see details of this project as HTML page](#i-want-to-see-details-of-this-project-as-html-page)
    - [I want to update the CONTRIBUTORS.rst file](#i-want-to-update-the-contributorsrst-file)
    - [I want to use dc-interface or ws-interface in my Java Maven project](#i-want-to-use-dc-interface-or-ws-interface-in-my-java-maven-project)
  - [Licenses](#licenses)
    - [Third party components](#third-party-components)

----

## CORE

The core of the platform contains all the business logic which handles
connections to the database, to the data collectors which provide the data and
to the web services which serve the data.

The core provides two components, the `writer`, that writes data to the database
and `reader`, that exposes REST APIs for web services. The new version of the
`reader` is called `ninja`, which is stored in another repository. Have a look
at the [Ninja
README.md](https://github.com/noi-techpark/it.bz.opendatahub.api.mobility-ninja/blob/master/README.md)
for details.

### WRITER
The writer is a REST API, which takes JSON DTOs, deserializes and validates them
and finally stores them in the database. Additionally, it sets stations to
active/inactive according to their presence inside the provided data. The writer
itself implements the methods to write data and is therefore the endpoint for
all data collectors. It uses the persistence-unit of the DAL which has full
permissions on the database.

The full API description can be found inside
[JsonController.java](writer/src/main/java/it/bz/idm/bdp/writer/JsonController.java).

Finally, we provide an interface to facilitate data collector development under
Java. See the next chapter for more details.

#### Authentication
We use Keycloak to authenticate. That service provides an `access_token` that
can be used to send POST requests to the writer. See the [Open Data Hub
Authentication / Quick
Howto](https://opendatahub.readthedocs.io/en/latest/guidelines/authentication.html#quick-howto)
for further details.

```sh
curl -X POST -L "https://auth.opendatahub.bz.it/auth/realms/noi/protocol/openid-connect/token" \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=password' \
    --data-urlencode 'username=my_username' \
    --data-urlencode 'password=my_password' \
    --data-urlencode 'client_id=odh-mobility-datacollector' \
    --data-urlencode 'client_secret=the_client_secret'
```

With this call you get an `access_token` that can then be used as follows in all
writer API methods. Here just an example to get all stations:

```sh
curl -X GET "https://share.mobility.api.opendatahub.bz.it/json/stations" \
    --header 'Content-Type: application/json' \
    --header 'Authorization: bearer your-access-token'
```

Write an email to `help@opendatahub.bz.it`, if you want to get the `client_secret`
and an Open Data Hub OAuth2 account.

#### dc-interface
The dc-interface contains the API through which components can communicate with
the BDP writer. Just include the `dc-interface` [maven
dependency](#i-want-to-use-dc-interface-or-ws-interface-in-my-java-maven-project)
in your project and use the existing [JSON client
implementation](dc-interface/src/main/java/it/bz/idm/bdp/json/JSONPusher.java).

The API contains several methods. We describe the most important methods here,
for the rest see
[JSONPusher.java](dc-interface/src/main/java/it/bz/idm/bdp/json/JSONPusher.java)
implementation.

**`Object getDateOfLastRecord(String stationCode,String dataType,Integer period)`**

This method is required to get the date of the last valid record


**`Object syncStations(List<StationDto> data)`**

This method is used to create, update, and deactivate stations of a specific
typology; `data` must be a list of StationDto's

**`Object syncDataTypes(List<DataTypeDto> data)`**

This method is used to create and update(and therefore upsert) data types;
`data` must be a list of DataTypeDto

**`Object pushData(DataMapDto<? extends RecordDtoImpl> dto)`**

This is the place, where you place all the data you want to pass to the writer.
The data in here gets saved in form of a tree.

Each branch can have multiple child branches, but can also have data itself,
which means it can have indefinite depth. Right now, by our internal conventions
we store everything on the second level, like this:

```
+- Station
   |
   +- DataType
      |
      `-Data
```

As value you can put a list of
[SimpleRecordDto.java](dto/src/main/java/it/bz/idm/bdp/dto/SimpleRecordDto.java),
which contains all the data points with a specific station and  a specific type.
Each point is represented as timestamp and value. To better understand the
structure, see the
[DataMapDto.java](dto/src/main/java/it/bz/idm/bdp/dto/DataMapDto.java)
source.

  
### READER

**NB: This is the old version of the reader, please use the new one instead, which is called `ninja`. Have a look at the [Ninja README.md](https://github.com/noi-techpark/it.bz.opendatahub.api.mobility-ninja/blob/master/README.md) for details.**

The reader is an API exposing data through a [JSON REST
service](ws-interface/src/main/java/it/bz/idm/bdp/ws/RestClient.java). It
depends on the DAL module and retrieves data by querying the underlying database
through JPA query language.

#### Authentication
The reader side handles authentication for non-opendata through an OAuth authentication mechanism using [JSON Web Tokens (JWT)](https://jwt.io/) through the [Java JWT library](https://github.com/jwtk/jjwt).

> If you want to access closed data, write a request to info@opendatahub.bz.it.

#### Authorization
Each user we create is associated to a *ROLE*, which has a set of permissions. The rules which define these permissions are inserted manually into `/dal/src/main/resources/META-INF/sql/opendatarules.sql`.

Two default roles exist:
- *ADMIN*, which has full access on all existing data in the database
- *GUEST*, which has restricted access to all data published under an open data license. Everybody who is not authenticated is therefore automatically using this role to access data.

#### ws-interface
If you create a Java client to access data from the reader's API, you may use
this library. Just include the `ws-interface` [maven
dependency](#i-want-to-use-dc-interface-or-ws-interface-in-my-java-maven-project)
in your project and implement the abstract [JSON client
implementation](ws-interface/src/main/java/it/bz/idm/bdp/ws/RestClient.java).


### DAL

DAL is the layer which communicates with the DB underneath used by the so called
reader and writer modules. The communication is handled through the ORM
Hibernate and its spatial component for geometries. The whole module got
developed using PostgreSQL as database and Postgis as an extension, but should
also work with other RDBMS (nobody knows :)).

Connection pooling is handled by
[HikariCP](https://github.com/brettwooldridge/HikariCP) for high speed
connections to the DB.

In some cases geometry transformations and elaborations were needed to be
executed on application level and therefore [Geotools](http://www.geotools.org/)
was added as dependency.

To configure the DAL module to communicate with your database you need to
provide configuration and credentials:
>`src/main/resources/META-INF/persistence.xml`

As you will see 2 persistence-units are configured. One is meant for the reader
with preferably a read-only user and the other one for the writer which performs
all the transactions.

We use a separate tool to generate the schema inside the database. See
`tools/README.md` for further details. The latest schema can always be found
within `dal/src/main/resources/META-INF/sql` with the name
`schema-<version>-dump.sql` and `schema-<version>-modifications.sql`. Execute
both scripts in that order.

Hibernate, our object-relational-mapping (ORM) framework, handles the schema
validation only (for security reasons).
> hibernate.hbm2ddl.auto = `validate`

See the [Installation guide](#installation-guide) for a fast initial setup.

The following chapters describe the most important entities: `station`, `data
type`, `record` and `edge`.

#### Station
The `station` represents the origin of the data which needs an identifier, a
name, a coordinate and a so called `stationtype`. It also should contain the
origin of the data, the current *active* state (if actively used or not) and if
it has a parent station, used to model a hierarchical station structure. For all
remaining data, which enriches the station, we created a field *metadata*. It
can hold any kind of meta information in form of a JSON object. To understand
the functionality and the main job of this entity check the source code
[Station.java](dal/src/main/java/it/bz/idm/bdp/dal/Station.java).

*Example*:
>A station can be of stationtype `MeteoStation`, has an identifier `89935GW` and
a position `latitude":46.24339407235059,"longitude":11.199431152658656`. It can
have additional information like address, municipality, opening times etc.,
which would be modelled as meta data entry.


#### DataType
The `data type` represents the typology of the data in form of an unique name
and a unit. Description and metric of measurements can also be provided.

*Example*:
>A `temperature` can have a unit `°C` and can be an `average` value of the last
>300 seconds (called `period`).

#### Record
A `record` represents a single measurement containing a `value`, a `timestamp` ,
a `data-type`, a `station`, and a `provenance`. Provenance indicates which data
collector in which version collected the data. It is needed to implement
traceability between collectors and inserted data, to identify data for
cleansing or bug fixes.

*Example*:
> We measure on `Fri Dec 16 2016 10:47:33` a data type `temperature` (see data
> type example) of `20.4` for a meteo station called `89935GW` (see station
> example).

#### Edge
An edge represents the spatial geometry between two stations. We model this
internally as a station triple: `origin, edge, destination`, because currently
only stations can be exposed through our API. We add a line-geometry to that
triple to describe the entity geographically. Hereby, `origin` and `destination`
are two stations of any type that represent two points on the map. The `edge` is
also a station of type `LinkStation`, that has no coordinates. It is the
description of the edge.

*Example*:
> A street between two stations, where the measured data could be how many cars
> passed it.

If you need more information about specific entities or classes, try to use the
javadoc or source code inside [DAL](dal).

### DTO
Data transfer objects (DTOs) are used to define the structure of the data
exchange. They are used between data provider and data persister (`writer`), but
also between data dispatcher and data reader (`reader`). They consist of fields
which are all primitives and easily serializable. The [DTO](dto) module is a
java library contained in all modules of the big data platform, simply because
it defines the communication structure in between.


The following chapters describe the most used DTOs.

#### StationDto
Describes a place where measurements get collected. It is the origin of the
data. We define the structure inside
[StationDto.java](dto/src/main/java/it/bz/idm/bdp/dto/StationDto.java).

#### DataTypeDto
Describes a specific type of data. We define the structure inside
[DataTypeDto.java](dto/src/main/java/it/bz/idm/bdp/dto/DataTypeDto.java)

#### SimpleRecordDto
Describes the measured value. We define the structure inside
[SimpleRecordDto.java](dto/src/main/java/it/bz/idm/bdp/dto/SimpleRecordDto.java)

## Installation guide

Use the
[quickbuild.sh](quickbuild.sh)
script to setup your environment. Usage, configuration and prerequisite
information can be found inside the script. The `SQLVERSION` variable specifies
the dump and modification SQL script versions inside DAL.

> The Postgres user `PGUSER` must have privileges to create schemas and tables!

Example:
```
PGUSER=postgres PGPASSWORD=yourpassword SQLVERSION=1.3.6 ./quickbuild.sh
```

If this works you made it.

You deployed your bdp-core and now will be able to add modules or develop your
own. If you want to fill your DB with data, you will either create your own
module, which works with the writer API or you can follow the guide on
https://github.com/noi-techpark/bdp-helloworld/tree/master/data-collectors/my-first-data-collector
where it's shown hot to use an already existing JAVA-client. If you also need to
expose this data you can either use the reader API or use the existing client
interface `ws-interface`.

## Flight rules

### I want to generate a new schema dump out of Hibernate's Entity classes

See
[README.md](tools/README.md)
inside `/tools`.

### I want to update license headers of each source file
To update license headers in each source code file run `mvn license:format`. To
configure the header template edit `LICENSE/templates/` files, and set the
correct attributes inside each `pom.xml`. See the plugin
[license-maven-plugin](http://code.mycila.com/license-maven-plugin/) homepage
for details. Use the `quicklicense.sh` script to update all source code license
headers at once.

### I want to see details of this project as HTML page
Run `mvn site` to create a HTML page with all details of this project. Results
can be found under `<project>/target/site/`, entrypoint is as usual
`index.html`.

### I want to update the CONTRIBUTORS.rst file
Just run `bash CONTRIBUTORS.rst` and check the output inside the file itself.
Configure any mail or name mappings inside `.mailmap`. See `man git shortlog`
for further details.

### I want to use dc-interface or ws-interface in my Java Maven project
Include the following snippet in your `pom.xml` file:
```
<repositories>
  <repository>
    <id>maven-repo.opendatahub.bz.it</id>
    <url>http://it.bz.opendatahub.s3-website-eu-west-1.amazonaws.com/release</url>
  </repository>
</repositories>
```

Include then, also the dependency (either `dc-interface` for data collectors or
`ws-interface` for read-only web services):
```
<dependency>
  <groupId>it.bz.idm.bdp</groupId>
  <artifactId>dc-interface</artifactId>
  <version>2.0.0</version>
</dependency>
```

You can also use a version-range, like `[2.0.0,3.0.0)`. Find the latest version
in our [release channel](https://github.com/noi-techpark/bdp-core/releases) on
GitHub.


## Licenses

### Third party components

- `CONTRIBUTORS.rst` script done by [Daniele Gobbetti](https://github.com/danielegobbetti)
