----

# DEPRECATED
The new home of Ninja is https://github.com/noi-techpark/ninja

----





























----

----

----

# API - Version 2

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

#### Table of Contents

- [I want to run the Ninja web-service](#i-want-to-run-the-ninja-web-service)
- [Station Types / Categories](#station-types--categories)
  - [I want to get all station types as a list](#i-want-to-get-all-station-types-as-a-list)
- [Stations](#stations)
  - [I want to get all e-charging stations including details](#i-want-to-get-all-e-charging-stations-including-details)
  - [I want to get all e-charging stations and their plugs including details](#i-want-to-get-all-e-charging-stations-and-their-plugs-including-details)
  - [I want to get all stations of any type including details](#i-want-to-get-all-stations-of-any-type-including-details)
- [Stations, Data Types and most up-to-date Measurements](#stations-data-types-and-most-up-to-date-measurements)
  - [I want to get all most up-to-date measurements of all parking lots](#i-want-to-get-all-most-up-to-date-measurements-of-all-parking-lots)
  - [I want to get all most up-to-date occupancy values of all parking lots](#i-want-to-get-all-most-up-to-date-occupancy-values-of-all-parking-lots)
- [Stations, Data Types and historical Measurements](#stations-data-types-and-historical-measurements)
  - [I want to get historical occupancy values of all parking lots from a certain period](#i-want-to-get-historical-occupancy-values-of-all-parking-lots-from-a-certain-period)
- [Pagination](#pagination)
- [Filtering with SELECT and WHERE](#filtering-with-select-and-where)
  - [I want to see only station names, data type names and the value of the measurement](#i-want-to-see-only-station-names-data-type-names-and-the-value-of-the-measurement)
  - [I want to see only parking stations within a bounding box of a map](#i-want-to-see-only-parking-stations-within-a-bounding-box-of-a-map)
  - [I want to see all information where the measured value is greater than 100 and the station origin is FAMAS](#i-want-to-see-all-information-where-the-measured-value-is-greater-than-100-and-the-station-origin-is-famas)
  - [I want to see all information where the station code starts with "me" or "rovereto"](#i-want-to-see-all-information-where-the-station-code-starts-with-me-or-rovereto)
  - [I want active creative industry stations with their sector and website, but only if the have one](#i-want-active-creative-industry-stations-with-their-sector-and-website-but-only-if-the-have-one)
  - [I want all creative industry station names, which do not have a sector assigned](#i-want-all-creative-industry-station-names-which-do-not-have-a-sector-assigned)
- [Null values](#null-values)
- [Representation](#representation)
- [Authentication](#authentication)
  - [I want to retrieve protected measurements (closed data)](#i-want-to-retrieve-protected-measurements-closed-data)
- [Additional Sample Queries](#additional-sample-queries)
  - [show all echarging stations of bolzano](#show-all-echarging-stations-of-bolzano)
  - [Show number of public, private and private with public access echarging stations](#show-number-of-public-private-and-private-with-public-access-echarging-stations)
  - [Show the total number of plugs and how many are currently available](#show-the-total-number-of-plugs-and-how-many-are-currently-available)
  - [Filter EchargingPlugs by voltage](#filter-echargingplugs-by-voltage)
  - [Filter EchargingStations by payment method](#filter-echargingstations-by-payment-method)
  - [Get all possible states of all echarging stations](#get-all-possible-states-of-all-echarging-stations)
  - [Filter EchargingStations by state](#filter-echargingstations-by-state)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## I want to run the Ninja web-service

### Prerequisites

- Java JDK 1.8 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x
- Run [NOI Authentication server](https://github.com/noi-techpark/authentication-server) locally or connect to test environment

### How to setup NOI Authentication Server locally?

- [Here](https://github.com/noi-techpark/authentication-server) you can find how to run the server locally
- Create a new realm following these [steps](https://github.com/noi-techpark/authentication-server/blob/master/docs/noi-authentication-server.md#realm)

#### How to register this application in your local authentication server?

1. Open the previously created realm
2. Create a new client (Clients -> Create)

| Property | Value           |
| -------- | --------------- |
| ClientID | odh-mobility-v2 |

3. Client Settings

| Property    | Value       |
| ----------- | ----------- |
| Access Type | bearer-only |

4. Navigate to Roles

Add following roles: BDP_ADMIN, BDP_BLC, BDP_MAD, BDP_CBZ

#### How to create a user or assign a user the necessary roles for this application?

1. Go to users
2. Create user or select user (View users)
3. Assign roles: Role Mappings -> Client Roles -> odh-mobility-v2

#### How to create a client to generate tokens for testing purposes?

1. Open the previously created realm
2. Create a new client (Clients -> Create)

| Property | Value               |
| -------- | ------------------- |
| ClientID | odh-mobility-client |

3. Client Settings

| Property                     | Value  |
| ---------------------------- | ------ |
| Access Type                  | public |
| Standard Flow Enabled        | Off    |
| Implicit Flow Enabled        | Off    |
| Direct Access Grants Enabled | On     |

4. Navigate to Scope

| Property                                          | Value                                  |
| ------------------------------------------------- | -------------------------------------- |
| Full Scope Allowed                                | Off                                    |
| Client Roles -> odh-mobility-v2 -> Assigned Roles | Move available roles to assigned roles |

5. Generate a new token

```sh
curl --location --request POST 'http://localhost:8080/auth/realms/noi/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username={USERNAME}' \
--data-urlencode 'password={PASSWORD}' \
--data-urlencode 'client_id=odh-mobility-client'
```

### How to start local development server?

Create `local` application properties profile.

```bash
cd src/main/resources
touch application-local.properties
```

Configure at least the mandatory properties in the newly created `application-local.properties` file, such as:

- spring.datasource.password

Now you can start the application with:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The server will startup and listen on `http://localhost:8081`.

## Station Types / Categories

### I want to get all station types as a list

```
GET /
```

## Stations

Please note, that the response is limited. However, you can [set another limit
or disable it completely](#pagination). The first path variable is the
[representation](#representation), which can be either `flat` or `tree`. For
compactness, we will use only flat representations throughout this tutorial.

### I want to get all e-charging stations including details

```
GET /flat/EChargingStation
```

### I want to get all e-charging stations and their plugs including details

```
GET /flat/EChargingStation,EChargingPlug
```

As you see an `EChargingStation` is a parent of `EchargingPlug`s, hence we could
avoid duplicate output, by simply fetching only plugs.

```
GET /flat/EChargingPlug
```

### I want to get all stations of any type including details

```
GET /flat/*
```

## Stations, Data Types and most up-to-date Measurements

### I want to get all most up-to-date measurements of all parking lots

```
GET /flat/ParkingStation/*
```

### I want to get all most up-to-date occupancy values of all parking lots

```
GET /flat/ParkingStation/occupied
```

## Stations, Data Types and historical Measurements

The URL pattern is `/station-types/data-types/from/to`, where `from` and `to`
form a half-open interval, i.e., `[from, to)`. This is important, if we want to
have a moving window over a timeline without selecting certain values multiple
times.

### I want to get historical occupancy values of all parking lots from a certain period

```
GET /flat/ParkingStation/occupied/2019-01-01/2019-01-02
```

```
GET /flat/ParkingStation/occupied/2019-01-01T23/2019-01-02
```

```
GET /flat/ParkingStation/occupied/2019-01-01/2019-01-02T12:30:15
```

The date format is `yyyy-MM-dd` or `yyyy-MM-ddThh:mm:ss.SSS`, where
`Thh:mm:ss.SSS` is optional and any part of it can be shortened from
left-to-right to any subset.

## Pagination

You can limit your output by adding `limit` to your request, and paginate your
results with an `offset`. If you want to disable the limit, set it to a negative
number, like `limit=-1`. Per default, the limit is set to a low number to
prevent excessive response times.

```
GET /flat/ParkingStation/occupied/2019-01-01/2019-01-02?limit=100&offset=300
```

## Filtering with SELECT and WHERE

It is possible to filter against JSON fields (columns in a database) with
`select=alias,alias,alias,...`, or per record (rows in a database) with
`where=filter,filter,filter,...`. The latter, is a conjunction (`and`) of all
clauses. Also complex logic is possible, with nested `or(...)` and `and(...)`
clauses, for instance `where=or(filter,filter,and(filter,filter))`.

**alias**
An `alias` is a list of point-separated-fields, where each field corresponds
to a step inside the JSON hierarchy. Internally, the first field represents the
database column and all subsequent fields drill into the JSON hierarchy.
For example, `metadata.municipality.cap` is an JSONB inside the database with a
column `metadata` and a JSONB object called `municipality` which has a `cap`
inside.

**filter**
A `filter` has the form `alias.operator.value_or_list`.

**value_or_list**

- `value`: Whatever you want, also a regular expression. Use double-quotes to
  force string recognition. Alternatively, you can escape characters `,`, `'`
  and `"` with a `\`. Use url-encoding, if your tool does not support certain
  characters. Special values are `null`, numbers and omitted values. Examples:
  - `description.eq.null`, checks if a description is not set
  - `description.eq.`, checks if a description is a string of length 0
- `list`: `(value,value,value)`

**operator**

- `eq`: Equal
- `neq`: Not Equal
- `lt`: Less Than
- `gt`: Greater Than
- `lteq`: Less Than Or Equal
- `gteq`: Greater Than Or Equal
- `re`: Regular Expression
- `ire`: Insensitive Regular Expression
- `nre`: Negated Regular Expression
- `nire`: Negated Insensitive Regular Expression
- `bbi`: Bounding box intersecting objects (ex., a street that is only partially
  covered by the box)
- `bbc`: Bounding box containing objects (ex., a station or street, that is
  completely covered by the box)
- `in`: True, if the value of the alias can be found within the given list.
  Example: `name.in.(Patrick,Rudi,Peter)`
- `nin`: False, if the value of the alias can be found within the given list.
  Example: `name.nin.(Patrick,Rudi,Peter)`

**logical operations**

- `and(filter,filter,...)`: Conjunction of filters (can be nested)
- `or(filter,filter,...)`: Disjunction of filters (can be nested)

Multiple conditions possible as comma-separated-values.

Example-syntax for `bbi` or `bbc` could be `coordinate.bbi.(11,46,12,47,4326)`, where
the ordering inside the list is left-x, left-y, right-x, right-y and SRID
(optional).

NB: Currently it is not possible to distinguish between a JSON field containing `null`
or a non-existing JSON field.

## Functions / Aggregation / Grouping

You can use any SQL function within **select**, which takes only a single
numeric value. All selected aliases, that are not within a function are used for
grouping.

Example: I want to have the `min`, `max`, `avg` and `count` of all data types of
e-charging stations.

```
GET /flat/EChargingStation/*?select=tname,min(mvalue),max(mvalue),avg(mvalue),count(mvalue)
```

NB: Currently only numeric functions are possible, we will not select anything
from our string measurements.

### I want to see only station names, data type names and the value of the measurement

```
GET /flat/ParkingStation/occupied/2019-01-01/2019-01-02?select=sname,tname,mvalue
```

### I want to see only parking stations within a bounding box of a map

```
GET /flat/ParkingStation/*?where=scoordinate.bbi.(11.63,46.0,11.65,47.0,4326)
```

... I want now to add to that query two additional stations (ex., 69440GW and AB3), that I
need regardless, if they are within the bounding box or not.

```
GET /flat/ParkingStation/*?where=or(scoordinate.bbi.(11.63,46.0,11.65,47.0,4326),scode.in.(69440GW,AB3))
```

### I want to see all information where the measured value is greater than 100 and the station origin is FAMAS

```
GET /flat/ParkingStation/occupied/2019-01-01/2019-01-02?where=mvalue.gt.100,sorigin.eq.FAMAS
```

Here the syntax for each clause is `attribute.operator.value`, where value can
be composed of any character except `,'"`, which must be escaped like `\,`, `\'`
or `\"`. A special value is `null`. If you want to use it as a literal value,
that is, the String itself, then you must put it into double-quotes, like
`"null"`.

### I want to see all information where the station code starts with "me" or "rovereto"

We use a key-insensitive regular expression here:

```
GET /flat/ParkingStation/occupied/2019-01-01/2019-01-02?where=scode.ire.(ME|Rovereto)
```

### I want active creative industry stations with their sector and website, but only if the have one

We use a JSON selector and JSON filters here:

```
GET /flat/CreativeIndustry?where=sactive.eq.true,smetadata.website.neq.null,smetadata.website.ire."http"&select=sname,smetadata.sector,smetadata.website
```

We check not only for `smetadata.website` to be present, but also to start with `http` to be sure it
is not a description telling us, that the website is currently under development or similar things.

### I want all creative industry station names, which do not have a sector assigned

We use a JSON selector and JSON filters here:

```
GET /flat/CreativeIndustry?where=smetadata.sector.eq.null&select=sname
```

## Null values

You can also see null-values within JSON, by adding `shownull=true` to your parameter list.

```
GET /flat/ParkingStation/occupied/2019-01-01/2019-01-02?shownull=true
```

## Representation

We have two types of representations: `flat` and `tree`. The former one shows
each JSON object with all selected attributes at the first level. Deeper levels
represent complex data types, such as `coordinates` and `jsonb`. Only the first
level can be selected or filtered.

Example with `select=stype,dtype,mvalue,smetadata`:

```json
{
  "data": [
    {
      "stype": "ParkingStation",
      "dtype": "occupied",
      "mvalue": 300,
      "smetadata": {
        "capacity": 1200,
        "...": "..."
      }
    }
  ]
}
```

As you can see, the station type `stype` and the data type `dtype` are on the
same level within the JSON object. These are first order attributes, whereas
`smetadata` is a `jsonb`-typed column.

If you want to retrieve only subsets of information, like `all data types`,
which do not match inside a hierarchy, this representation is suited for you.

```json
{
  "data": [
    {
      "dtype": "ParkingStation"
    },
    {
      "dtype": "VMS"
    },
    {
      "dtype": "EChargingStation"
    }
  ]
}
```

The `tree` representation, shows a hierarchy of the following kind:

```
station types / categories
└── stations
    └── data types
        └── measurements
```

NB: The `tree` is more expensive to generate on the server and use within your
application, but the response size can be much smaller due to nesting and thus
duplicate attribute elimination. However, some queries do not match that
hierarchy, so the `flat` representation is more suited for them.

## Authentication

We use a token based authentication (JWT) which can be retrieved from an OAuth
2.0 server.

### I want to retrieve protected measurements (closed data)

NB: Swagger does not support authentication yet, therefore we provide a `curl`
example.

```sh
curl -X GET "https://example.com/tree/VMS/*" \
     -H 'Authorization: Bearer header.payload.signature'
```

## Additional Sample Queries

For better readability, we assume that all queries are configured as follows, if
not otherwise stated: `shownull=false&distinct=true&limit=-1`.

NB: We need to count results on application level, because the API does
currently not support aggregation, like `count` and other statistical methods
involving `grouping`.

### show all echarging stations of bolzano

```
GET /flat/EChargingStation?where=sactive.eq.true,scoordinate.bbi.(11.27539,46.444913,11.432577,46.530384)
```

### Show number of public, private and private with public access echarging stations

Since we want to count the results later, we need to set `distinct=false`.

```
GET /flat/EChargingStation?select=smetadata.accessType&where=sactive.eq.true&distinct=false
```

### Show the total number of plugs and how many are currently available

This means that the measured value `mvalue` must be equal `1`.

```
GET /flat/EChargingPlug/*?select=scode&where=sactive.eq.true,tname.eq.echarging-plug-status,mvalue.eq.1
```

### Filter EchargingPlugs by voltage

```
GET /flat/EChargingPlug?where=sactive.eq.true
```

### Filter EchargingStations by payment method

```
GET /flat/EChargingStation?where=sactive.eq.true,smetadata.accessType.eq.PUBLIC
```

### Get all possible states of all echarging stations

```
GET /flat/EChargingStation?select=smetadata.state
```

### Filter EchargingStations by state

For example filter against `ACTIVE` states.

```
GET /flat/EChargingPlug?where=sactive.eq.true,smetadata.state.eq.ACTIVE
```
