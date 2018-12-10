# PostgreSQL Schema Migration from v1 to v2

## Known issues of the current SQL migration script
- user postgres not found (should be bdp)
- user bdpreadonly not found (should be bdp_readonly)
- make sure, that we get all needed indexes right


## Tables as they were inside the old `intime` schema

> NB: We strip nulls from JSON metadata, that is, every fields that are null
> inside the old PostgreSQL tables, will not be present at all inside JSON.

> NB: Basic Data Tables will be integrated into `station.metadata` and
> `station_id` fields into parent columns of stations


### alarm
Removed

### alarmspecification
Removed

### bdppermissions_oldtable
Removed

### bdprole
No changes

### bdprules
No changes

### bdpuser
No changes

### bdpusers_bdproles
No changes

### bicyclebasicdata
- Copy `bikesharingstation_id` into `parent_id` of bicycle stations, that can be
  found in `station_id`
- Removed

### bikesharingstationbasicdata
- Create a JSON out of each `max_available` of all `type_id` and insert it into
  station's metadata field
- Removed

Example:
```
{
  "number-available": "28",
  "mountain-bike-adult": "4",
  "mountain-bike-child": "8",
  "mountain-bike-teenager": "3",
  "city-bike-adult-with-gears": "9",
  "city-bike-adult-without-gears": "5"
}
```

### bluetoothbasicdata
Removed (no data)

### carparkingbasicdata
- Copy all fields into json metadata of stations
- except `area`, which becomes `{srid:4326,geotype:MULTIPOLYGON,dims:2,area:<geometry_data>}`
- Removed

Example (`area` is artificial data):
```
{
  "disabledcapacity": 29,
  "owneroperator": "SEAB",
  "parkingtype": "Public",
  "capacity": 1015,
  "phonenumber": "0471 301850",
  "mainaddress": "Via Mayr Josef Nusser",
  "state": 1,
  "womancapacity": 80,
  "area": {
    "srid": 4326,
    "geotype": "MULTIPOLYGON",
    "dims": 2,
    "area": "<geometry_data>"
  }
}
```

### carparkingdynamic & carparkingdynamichistory
- Ignore `carparkstate`, `carparktrend`, `exitrate`, `fillrate` (always null)
- Ignore `occupacypercentage` (can be calculated out of meta data information
  and we do not care about changing sizes of parking facilities ATM)
- Create a new type `occupacy`
- Insert `occupacy` into measurment + measurmenthistory tables with a `period`
  of 300 seconds, since the lastupdate column shows on average those differences
- Removed

### carpoolinghubbasicdata & carpoolinghubbasicdata_translation
- `station_id` becomes the `parent_id` of each carpoolinguserbasicdata
  (it is always a 1:1 relation)
- Copy all other fields form a join with translation tables get put into json
  with translations as sub-objects
- Removed

Example:
```
{
  "location": {
    "it": {
      "city": "Merano",
      "address": "Incrocio Via Piave / Via Petrarca",
      "name": "Incrocio Via Piave / Via Petrarca"
    }
  }
}
```

### carpoolinguserbasicdata & carpoolinguserbasicdata_translation
- Copy all fields into json metadata and translations as sub-objects
- Removed

Example:
```
{
  "gender": "M",
  "name": "Mario",
  "pendular": true,
  "type": "P",
  "arrival": "14:00",
  "departure": "0",
  "location": {
    "it": {
      "city": "Merano",
      "address": "Via Stazione Centrale 1"
    }
  }
}
```

### carsharingcarstationbasicdata
- Copy `station_id`, which represents a car, into stations with ID
  `carsharingstation_id`, which are places where cars be taken
- Copy all fields into json metadata
- Removed

Example:
```
{
  "brand": "VW Golf Variant",
  "licenseplate": "FB 454 GH"
}
```

### carsharingstationbasicdata
- Copy all fields into json metadata of stations with ID `station_id`
- Removed

Example:
```
{
  "canbookahead": false,
  "companyshortname": "Car Sharing Südtirol Alto Adige",
  "hasfixedparking": true,
  "parking": 1,
  "spontaneously": false
}
```

### classification
- No changes
- We could think of moving this into a separate schema, if it belongs to a
  certain data collector or elaboration module

### copert_emisfact
- No changes
- We could think of moving this into a separate schema, if it belongs to a
  certain data collector or elaboration module

### copert_parcom
- No changes
- We could think of moving this into a separate schema, if it belongs to a
  certain data collector or elaboration module

### datatype_i18n
- Removed
- We could think of a good implementation for i18n support
- Reason for removal: translations as `en` with italian words and some that do
  not contain translations, but additional information.

### echargingplugbasicdata
- Copy all fields into json metadata of stations with ID `station_id`
- Removed

Example:
```
{
  "maxcurrent": 31,
  "maxpower": 7015,
  "mincurrent": 6,
  "plugtype": "IEC 62196-2 type 2 outlets (all amperage and phase)"
}
```

### echargingplugoutlet
- Copy all fields into json metadata of stations with ID `plug_id`: Add a key
  `outlets` with an array containing the all fields. See `outlets` below, the
  rest comes from the *echargingplugbasicdata* and other processings.
- Removed

Example:
```
{
  "outlets": [
    {
      "code": "2",
      "maxpower": 22,
      "plugtype": "Type2Mennekes",
      "maxcurrent": 31,
      "mincurrent": 0,
      "hasfixedcable": false
    }
  ],
  "maxpower": 7015,
  "plugtype": "IEC 62196-2 type 2 outlets (all amperage and phase)",
  "maxcurrent": 31,
  "mincurrent": 6,
  "municipality": "Bolzano - Bozen"
}
```

### echargingstationbasicdata
- Copy all fields into json metadata of stations with ID `station_id`
- Removed

Example:
```
{
  "city": "Avelengo - Hafling",
  "state": "AVAILABLE",
  "address": "Via Falzeben, 225",
  "accessinfo": "24/7",
  "accesstype": "PRIVATE_WITHPUBLICACCESS",
  "categories": "SLEEP&CHARGE",
  "reservable": false,
  "paymentinfo": "https://evway.net/app/",
  "assetprovider": "Route220",
  "chargingpointscount": 2
}
```

### elaboration & elaborationhistory

> Our first idea was the following:
> - Move into `measurement` with `origin = NOI`
> - Move into `measurmenthistory` with `origin = NOI`
> - Removed
>
> However, it is not feasible, since we cannot change the origin of stations
> to NOI, if they have already another origin associated.

Final solution:
- Create a new entity, called `provenance` with columns = `(lineage, dataCollector, dataCollectorVersion)`
- Insert into `provenance`: `('NOI', 'Migration from V1', null)`
- Add a foreign key from any `measurment...` table to `provenance`
- Move `elaboration` into `measurement` with `provenance = (NOI, Migration from V1)`
- Move `elaborationhistory` into `measurementhistory` with `provenance = (NOI, Migration from V1)`
- Do not move data, that has `created_on` or `value` null
- Remove `elaboration` and `elaborationhistory`


### linkbasicdata
- Copy `id`, `station_id`, `origin_id`, `linegeometry` and `destination_id` to
  `edge`, since we need a generic way to model connections between stations
  (maybe we should also call the `station` table, `node` in the future)
- Copy `length`, `street_ids_ref`, and `elapsed_time_default` to json metadata
  of station with ID `station_id` (see example)
- Add a column `directed` to understand if this records model a directed or
  undirected graph
- Removed

Example:
```
{
  "length": 2952.52065799999991,
  "street_ids_ref": "|1|4|40|54|",
  "elapsed_time_default": 300
}
```

### measurement
- Rename column `value` to `doublevalue`

### measurmenthistory
- Rename column `value` to `doublevalue`

### measurementmobile & measurementmobilehistory
- We have around 7,000,000 records * 61 attributes, which would become 61 types
  and 427,000,000 new records inside measurementhistory.
  This could be a huge performance issue.
- We keep these tables without changes for now. They will be accessible solely
  via PostgreSQL (ommitting Rest API connectivity)

### measurementstring
- Rename column `value` to `stringvalue`

### measurementstringhistory
- Rename column `value` to `stringvalue`

### meteostationbasicdata
- Copy all fields into json metadata of stations with ID `station_id`
- Removed

Example:
```
{
  "area": "Unterer Eisack",
  "zeus": "69900MS"
}
```

### scheduler_run
- No changes
- We could think of moving this into a separate schema, if it belongs to a
  certain data collector or elaboration module

### scheduler_task
- No changes
- We could think of moving this into a separate schema, if it belongs to a
  certain data collector or elaboration module

### scheduler_task_deps
- No changes
- We could think of moving this into a separate schema, if it belongs to a
  certain data collector or elaboration module

### scheduler_worker
- No changes
- We could think of moving this into a separate schema, if it belongs to a
  certain data collector or elaboration module

### schemaversion
- Update automatically from modification-2.0.0 SQL script inside bdp-core/dal

### station
- maybe we should call the `station` table `node` in the future (see
  linkbasicdata)
- Remove `shortname` (never used)
- Remove `st_old_id` (not important anymore)
- Copy `description` and `municipality` to metadata

### streetbasicdata
- Copy all fields into json metadata of stations with ID `station_id`
- Maybe we should move linegeometry into the `station` table, call the column
  `pointprojection` `geometry` and insert all geometry related data their
- Removed

Example:
```
{
  "length": 71,
  "old_idstr": 73,
  "description": " PIAZZA GRIES / GRIESER PLATZ",
  "linegeometry": "0102000020787F00000200000....B88282B9244173C65425D7A75341",
  "speed_default": 41
}
```

### trafficstreetfactor
- Create a new station with `stationtype = TrafficStreetFactor` with
  `factor`, `length` and `hv_perc` as json metadata (see example)
  NB: These stations do not have an `origin` yet
- Copy `id_arco` (becomes `origin_id`), `id_spira` (becomes `destination_id`)
  and the new `station_id` from the creation before (point 1) into the `edge`
  table with `directed = false`
- Removed

Example:
```
{
  "factor": 1.04,
  "length": 937,
  "hv_perc": 7
}
```

### translation
- Removed, since it is integrated into metadata of carpooling translations now

### type
- Remove `timestamp` (was always null)
