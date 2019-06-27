# API - Version 2

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
#### Table of Contents

- [I want to run the reader2 web-service](#i-want-to-run-the-reader2-web-service)
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
- [Filtering](#filtering)
  - [I want to see only station names, data type names and the value of the measurement](#i-want-to-see-only-station-names-data-type-names-and-the-value-of-the-measurement)
  - [I want to see only parking stations and their coordinates within a bounding box of a map](#i-want-to-see-only-parking-stations-and-their-coordinates-within-a-bounding-box-of-a-map)
  - [I want to see only parking stations and their coordinates within a bounding box of a map](#i-want-to-see-only-parking-stations-and-their-coordinates-within-a-bounding-box-of-a-map-1)
  - [I want to see all information where the measured value is greater than 100 and the station origin is FAMAS](#i-want-to-see-all-information-where-the-measured-value-is-greater-than-100-and-the-station-origin-is-famas)
  - [I want to see all information where the station code starts with "me" or "rovereto"](#i-want-to-see-all-information-where-the-station-code-starts-with-me-or-rovereto)
- [Null values](#null-values)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## I want to run the reader2 web-service

Go to `src/main/resources/` and copy `database.properties.dist` to
`database.properties` and configure it accordingly. Mostly `jdbcUrl`, `username`
and `password` are necessary, the rest can be kept as is.

Within the same directory, open `application.properties` and set the server port
and log levels.

Run `mvn spring-boot:run`.

## Station Types / Categories

### I want to get all station types as a list
```
GET /
```

## Stations

Please note, that the reponse is limited. However, you can [set another limit or
disable it completely](#pagination).

### I want to get all e-charging stations including details
```
GET /EChargingStation
```

### I want to get all e-charging stations and their plugs including details
```
GET /EChargingStation,EChargingPlug
```

As you see an `EChargingStation` is a parent of `EchargingPlug`s, hence we could
avoid duplicate output, by simply fetching only plugs.

```
GET /EChargingPlug
```

### I want to get all stations of any type including details
```
GET /*
```

## Stations, Data Types and most up-to-date Measurements

### I want to get all most up-to-date measurements of all parking lots
```
GET /ParkingStation/*
```

### I want to get all most up-to-date occupancy values of all parking lots
```
GET /ParkingStation/occupied
```

## Stations, Data Types and historical Measurements

The URL pattern is `/station-types/data-types/from/to`, where `from` and `to`
form a half-open interval, i.e., `[from, to)`. This is important, if we want to
have a moving window over a timeline without selecting certain values multiple
times.

### I want to get historical occupancy values of all parking lots from a certain period
```
GET /ParkingStation/occupied/2019-01-01/2019-01-02
```

```
GET /ParkingStation/occupied/2019-01-01T23/2019-01-02
```

```
GET /ParkingStation/occupied/2019-01-01/2019-01-02T12:30:15
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
GET /ParkingStation/occupied/2019-01-01/2019-01-02?limit=100&offset=300
```

## Filtering

Some JSON attributes can be selected, that is, all those which start with a
table prefix. For instance, all station related fields start with `s`, all
parent related fields with an `p`, measurements with an `m` and data types with
an `t`. All other are generated JSON blocks, that cannot be selected yet.

It is possible to filter against JSON fields (columns in a database) with
`select`, or per record (rows in a database) with `where`. The latter, is a
conjunction (`and`) of all clauses. Also complex logic is possible, with nested
`or(...)` and `and(...)` clauses.

Filter operators for the `where` clause are as follows:
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
- `and(alias.operator.value,...)`: Conjunction of filters (can be nested)
- `or(alias.operator.value,...)`: Disjunction of filters (can be nested)

Multiple conditions possible as comma-separated-values.

Example-syntax for bbi/bbc could be `coordinate.bbi.(11,46,12,47,4326)`, where
the ordering inside the list is left-x, left-y, right-x, right-y and SRID
(optional).


### I want to see only station names, data type names and the value of the measurement
```
GET /ParkingStation/occupied/2019-01-01/2019-01-02?select=sname,tname,mvalue
```

### I want to see only parking stations and their coordinates within a bounding box of a map
```
GET /ParkingStation/*?where=scoordinate.bbi.(11.63,46.0,11.65,47.0,4326)
```

... I want now to add to that query two additional stations (ex., 69440GW and AB3), that I
need regardless, if they are within the bounding box or not.

```
GET /ParkingStation/*?where=or(scoordinate.bbi.(11.63,46.0,11.65,47.0,4326),scode.in.(69440GW,AB3))
```

### I want to see only parking stations and their coordinates within a bounding box of a map


### I want to see all information where the measured value is greater than 100 and the station origin is FAMAS
```
GET /ParkingStation/occupied/2019-01-01/2019-01-02?where=mvalue.gt.100,sorigin.eq.FAMAS
```

Here the syntax for each clause is `attribute.operator.value`, where value can
be composed of any character except `,` and `'`, which must be escaped like `\,`
and `\'`. A special value is `null`. Currently, it is therefore not possible to
search for a string `"null"`.

### I want to see all information where the station code starts with "me" or "rovereto"

We use a key-insensitive regular expression here:
```
GET /ParkingStation/occupied/2019-01-01/2019-01-02?where=scode.ire.(ME|Rovereto)
```

## Null values

You can also see null-values within JSON, by adding `shownull=true` to your parameter list.

```
GET /ParkingStation/occupied/2019-01-01/2019-01-02?shownull=true
```



