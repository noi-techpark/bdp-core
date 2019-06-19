# API - Version 2

## API Description

### Station Types / Categories

#### I want to get all station types as a list
```
GET /
```

### Stations

#### I want to get all e-charging stations including details
```
GET /EChargingStation
```

#### I want to get all e-charging stations and their plugs including details
```
GET /EChargingStation,EChargingPlug
```

As you see an `EChargingStation` is a parent of `EchargingPlug`s, hence we
could avoid duplicate output, by simply fetching only plugs.

```
GET /EChargingPlug
```

#### I want to get all stations of any type including details
```
GET /*
```

### Stations, Data Types and most up-to-date Measurements

#### I want to get all most up-to-date measurements of all parking lots
```
GET /ParkingStation/*
```

#### I want to get all most up-to-date occupancy values of all parking lots
```
GET /ParkingStation/occupied
```

### Stations, Data Types and historical Measurements

#### I want to get historical occupancy values of all parking lots from a certain period
```
GET /ParkingStation/occupied/2019-01-01/2019-01-02
```

```
GET /ParkingStation/occupied/2019-01-01T23/2019-01-02
```

```
GET /ParkingStation/occupied/2019-01-01/2019-01-02T12:30:15
```

The date format is `yyyy-MM-dd` or `yyyy-MM-ddThh:mm:ss.SSS`, where `Thh:mm:ss.SSS`
is optional and any part of it can be shortened from left-to-right to any subset.

### Pagination

You can limit your output by adding `limit` to your request, and paginate your results with an `offset`.

```
GET /ParkingStation/occupied/2019-01-01/2019-01-02?limit=100&offset=300
```

### Filtering

Some JSON attributes can be selected, that is, all those which start with a table prefix. For instance,
all station related fields start with `s`, all parent related fields with an `p`, measurements with an `m`
and data types with an `t`. All other are generated JSON blocks, that cannot be selected yet.

It is possible to filter against JSON fields (columns in a database) with `select`,
or per record (rows in a database) with `where`. The latter, is a conjunction (`and`) of all clauses.
Disjunctions (`or`) or complex logic is currently not supported.

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

#### I want to see only station names, data type names and the value of the measurement
```
GET /ParkingStation/occupied/2019-01-01/2019-01-02?select=sname,tname,mvalue
```

#### I want to see all information where the measured value is greater than 100 and the station origin is FAMAS
```
GET /ParkingStation/occupied/2019-01-01/2019-01-02?where=mvalue.gt.100,sorigin.eq.FAMAS
```

Here the syntax for each clause is `attribute.operator.value`, where value can be composed of any character
except `,`, which must be escaped like `\,`. A special value is `null`. Currently, it is therefore not possible
to search for a string `"null"`.

#### I want to see all information where the station code starts with `me` or `rovereto` (key-insensitive)
```
GET /ParkingStation/occupied/2019-01-01/2019-01-02?where=scode.ire.(ME|Rovereto)
```

### Null values

You can also see null-values within JSON, by adding `shownull=true` to your parameter list.

```
GET /ParkingStation/occupied/2019-01-01/2019-01-02?shownull=true
```



