--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.10
-- Dumped by pg_dump version 9.6.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: intime; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA intime;


ALTER SCHEMA intime OWNER TO postgres;

--
-- Name: alarm_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.alarm_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.alarm_seq OWNER TO bdp;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: alarm; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.alarm (
    id bigint DEFAULT nextval('intime.alarm_seq'::regclass) NOT NULL,
    createdate timestamp without time zone,
    "timestamp" timestamp without time zone,
    specification_id bigint,
    station_id bigint
);


ALTER TABLE intime.alarm OWNER TO bdp;

--
-- Name: alarmspecification_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.alarmspecification_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.alarmspecification_seq OWNER TO bdp;

--
-- Name: alarmspecification; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.alarmspecification (
    id bigint DEFAULT nextval('intime.alarmspecification_seq'::regclass) NOT NULL,
    description character varying(255),
    name character varying(255)
);


ALTER TABLE intime.alarmspecification OWNER TO bdp;

--
-- Name: basicdata_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.basicdata_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.basicdata_seq OWNER TO bdp;

--
-- Name: bdppermissions; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.bdppermissions (
    uuid bigint NOT NULL,
    period integer,
    role_id bigint,
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.bdppermissions OWNER TO bdp;

--
-- Name: bdprole_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.bdprole_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.bdprole_seq OWNER TO bdp;

--
-- Name: bdprole; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.bdprole (
    id bigint DEFAULT nextval('intime.bdprole_seq'::regclass) NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    parent_id bigint
);


ALTER TABLE intime.bdprole OWNER TO bdp;

--
-- Name: bdprules_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.bdprules_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.bdprules_seq OWNER TO bdp;

--
-- Name: bdprules; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.bdprules (
    id bigint DEFAULT nextval('intime.bdprules_seq'::regclass) NOT NULL,
    period integer,
    role_id bigint,
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.bdprules OWNER TO bdp;

--
-- Name: bdpuser_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.bdpuser_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.bdpuser_seq OWNER TO bdp;

--
-- Name: bdpuser; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.bdpuser (
    id bigint DEFAULT nextval('intime.bdpuser_seq'::regclass) NOT NULL,
    email character varying(255) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    password character varying(255) NOT NULL,
    tokenexpired boolean DEFAULT false NOT NULL
);


ALTER TABLE intime.bdpuser OWNER TO bdp;

--
-- Name: bdpusers_bdproles; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.bdpusers_bdproles (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE intime.bdpusers_bdproles OWNER TO bdp;

--
-- Name: bicyclebasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.bicyclebasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    bikesharingstation_id bigint,
    type_id bigint
);


ALTER TABLE intime.bicyclebasicdata OWNER TO bdp;

--
-- Name: bikesharingstationbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.bikesharingstationbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    max_available integer,
    type_id bigint
);


ALTER TABLE intime.bikesharingstationbasicdata OWNER TO bdp;

--
-- Name: bluetoothbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.bluetoothbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    sim_number character varying(255),
    sim_serial character varying(255)
);


ALTER TABLE intime.bluetoothbasicdata OWNER TO bdp;

--
-- Name: carparkingbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.carparkingbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    area public.geometry,
    capacity integer,
    disabledcapacity integer,
    disabledtoiletavailable boolean,
    email character varying(255),
    mainaddress character varying(255),
    owneroperator character varying(255),
    parkingtype character varying(255),
    permittedvehicletypes character varying(255),
    phonenumber character varying(255),
    state integer,
    toiletsavailable boolean,
    url character varying(255),
    womencapacity integer
);


ALTER TABLE intime.carparkingbasicdata OWNER TO bdp;

--
-- Name: carparkingdynamic_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.carparkingdynamic_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.carparkingdynamic_seq OWNER TO bdp;

--
-- Name: carparkingdynamic; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.carparkingdynamic (
    id integer DEFAULT nextval('intime.carparkingdynamic_seq'::regclass) NOT NULL,
    carparkstate character varying(255),
    carparktrend character varying(255),
    createdate timestamp without time zone,
    exitrate double precision,
    fillrate double precision,
    lastupdate timestamp without time zone,
    occupacy integer,
    occupacypercentage integer,
    station_id bigint
);


ALTER TABLE intime.carparkingdynamic OWNER TO bdp;

--
-- Name: carparkingdynamichistory_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.carparkingdynamichistory_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.carparkingdynamichistory_seq OWNER TO bdp;

--
-- Name: carparkingdynamichistory; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.carparkingdynamichistory (
    id integer DEFAULT nextval('intime.carparkingdynamichistory_seq'::regclass) NOT NULL,
    carparkstate character varying(255),
    carparktrend character varying(255),
    createdate timestamp without time zone,
    exitrate double precision,
    fillrate double precision,
    lastupdate timestamp without time zone,
    occupacy integer,
    occupacypercentage integer,
    station_id bigint
);


ALTER TABLE intime.carparkingdynamichistory OWNER TO bdp;

--
-- Name: carpoolinghubbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.carpoolinghubbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint
);


ALTER TABLE intime.carpoolinghubbasicdata OWNER TO bdp;

--
-- Name: carpoolinghubbasicdata_translation; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.carpoolinghubbasicdata_translation (
    carpoolinghubbasicdata_id bigint NOT NULL,
    i18n_id bigint NOT NULL,
    i18n_key character varying(255) NOT NULL
);


ALTER TABLE intime.carpoolinghubbasicdata_translation OWNER TO bdp;

--
-- Name: carpoolinguserbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.carpoolinguserbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    arrival character varying(255),
    cartype character varying(255),
    departure character varying(255),
    gender character(1),
    name character varying(255),
    pendular boolean,
    type character varying(255),
    hub_id bigint
);


ALTER TABLE intime.carpoolinguserbasicdata OWNER TO bdp;

--
-- Name: carpoolinguserbasicdata_translation; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.carpoolinguserbasicdata_translation (
    carpoolinguserbasicdata_id bigint NOT NULL,
    location_id bigint NOT NULL,
    location_key character varying(255) NOT NULL
);


ALTER TABLE intime.carpoolinguserbasicdata_translation OWNER TO bdp;

--
-- Name: carsharingcarstationbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.carsharingcarstationbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    brand character varying(255),
    licenseplate character varying(255),
    carsharingstation_id bigint
);


ALTER TABLE intime.carsharingcarstationbasicdata OWNER TO bdp;

--
-- Name: carsharingstationbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.carsharingstationbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    canbookahead boolean,
    companyshortname character varying(255),
    hasfixedparking boolean,
    parking integer,
    spontaneously boolean
);


ALTER TABLE intime.carsharingstationbasicdata OWNER TO bdp;

--
-- Name: datatype_i18n; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.datatype_i18n (
    datatype_id bigint NOT NULL,
    i18n character varying(255),
    i18n_key character varying(255) NOT NULL
);


ALTER TABLE intime.datatype_i18n OWNER TO bdp;

--
-- Name: echargingplugbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.echargingplugbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    estation_id bigint
);


ALTER TABLE intime.echargingplugbasicdata OWNER TO bdp;

--
-- Name: echargingplugoutlet_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.echargingplugoutlet_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.echargingplugoutlet_seq OWNER TO bdp;

--
-- Name: echargingplugoutlet; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.echargingplugoutlet (
    id bigint DEFAULT nextval('intime.echargingplugoutlet_seq'::regclass) NOT NULL,
    code character varying(255),
    hasfixedcable boolean,
    maxcurrent double precision,
    maxpower double precision,
    mincurrent double precision,
    plugtype character varying(255),
    plug_id bigint
);


ALTER TABLE intime.echargingplugoutlet OWNER TO bdp;

--
-- Name: echargingstationbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.echargingstationbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    accessinfo character varying(255),
    accesstype character varying(255),
    address character varying(255),
    assetprovider character varying(255),
    categories character varying(255),
    chargingpointscount integer,
    city character varying(255),
    flashinfo character varying(255),
    locationserviceinfo character varying(255),
    paymentinfo text,
    reservable boolean,
    state character varying(255)
);


ALTER TABLE intime.echargingstationbasicdata OWNER TO bdp;

--
-- Name: elaboration_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.elaboration_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.elaboration_seq OWNER TO bdp;

--
-- Name: elaboration; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.elaboration (
    id integer DEFAULT nextval('intime.elaboration_seq'::regclass) NOT NULL,
    created_on timestamp without time zone,
    period integer,
    "timestamp" timestamp without time zone,
    value double precision,
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.elaboration OWNER TO bdp;

--
-- Name: elaborationhistory_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.elaborationhistory_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.elaborationhistory_seq OWNER TO bdp;

--
-- Name: elaborationhistory; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.elaborationhistory (
    id bigint DEFAULT nextval('intime.elaborationhistory_seq'::regclass) NOT NULL,
    created_on timestamp without time zone,
    period integer,
    "timestamp" timestamp without time zone,
    value double precision,
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.elaborationhistory OWNER TO bdp;

--
-- Name: linkbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.linkbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    elapsed_time_default integer,
    length double precision,
    linegeometry public.geometry,
    street_ids_ref character varying(255),
    destination_id bigint,
    origin_id bigint
);


ALTER TABLE intime.linkbasicdata OWNER TO bdp;

--
-- Name: measurement_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.measurement_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.measurement_seq OWNER TO bdp;

--
-- Name: measurement; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.measurement (
    id bigint DEFAULT nextval('intime.measurement_seq'::regclass) NOT NULL,
    created_on timestamp without time zone,
    period integer,
    "timestamp" timestamp without time zone,
    value double precision,
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.measurement OWNER TO bdp;

--
-- Name: measurementhistory_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.measurementhistory_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.measurementhistory_seq OWNER TO bdp;

--
-- Name: measurementhistory; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.measurementhistory (
    id bigint DEFAULT nextval('intime.measurementhistory_seq'::regclass) NOT NULL,
    created_on timestamp without time zone,
    period integer,
    "timestamp" timestamp without time zone,
    value double precision,
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.measurementhistory OWNER TO bdp;

--
-- Name: trafficvehiclerecord_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.trafficvehiclerecord_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.trafficvehiclerecord_seq OWNER TO bdp;

--
-- Name: measurementmobile; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.measurementmobile (
    id bigint DEFAULT nextval('intime.trafficvehiclerecord_seq'::regclass) NOT NULL,
    af_1_sccm double precision,
    af_1_valid_b boolean,
    can_acc_lat_mean_mps2 double precision,
    can_acc_lat_mps2 double precision,
    can_acc_lat_var_m2ps4 double precision,
    can_acc_long_mean_mps2 double precision,
    can_acc_long_mps2 double precision,
    can_acc_long_var_m2ps4 double precision,
    can_speed_mps double precision,
    can_valid_b boolean,
    co_1_ppm double precision,
    co_1_runtime_s integer,
    co_1_valid_b boolean,
    created_on timestamp without time zone,
    gps_1_alt_m double precision,
    gps_1_hdg_deg double precision,
    gps_1_lat_deg double precision,
    gps_1_long_deg double precision,
    gps_1_pdop_nr double precision,
    gps_1_sat_nr integer,
    gps_1_speed_mps double precision,
    gps_1_valid_b boolean,
    id_driver_nr integer,
    id_runtime_s integer,
    id_status_char character varying(255),
    id_system_nr integer,
    id_vehicle_nr integer,
    id_version_char character varying(255),
    imu_acc_lat_mean_mps2 double precision,
    imu_acc_lat_mps2 double precision,
    imu_acc_lat_var_m2ps4 double precision,
    imu_acc_long_mean_mps2 double precision,
    imu_acc_long_mps2 double precision,
    imu_acc_long_var_m2ps4 double precision,
    imu_speed_mps double precision,
    imu_valid_b boolean,
    no2_1_microgm3_exp double precision,
    no2_1_microgm3_ma double precision,
    no2_1_ppb double precision,
    no2_1_runtime_s integer,
    no2_1_valid_b boolean,
    no2_2_ppb double precision,
    no2_2_runtime_s integer,
    no2_2_valid_b boolean,
    o3_1_ppb double precision,
    o3_1_runtime_s integer,
    o3_1_valid_b boolean,
    "position" public.geometry,
    realtime_delay bigint,
    res_1_a double precision,
    res_1_runtime_s integer,
    res_1_valid_b boolean,
    res_2_a double precision,
    res_2_runtime_s integer,
    res_2_valid_b boolean,
    rh_1_pct double precision,
    rh_1_valid_b boolean,
    temp_1_c double precision,
    temp_1_valid_b boolean,
    ts_ms timestamp without time zone,
    station_id bigint
);


ALTER TABLE intime.measurementmobile OWNER TO bdp;

--
-- Name: trafficvehiclerecordhistory_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.trafficvehiclerecordhistory_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.trafficvehiclerecordhistory_seq OWNER TO bdp;

--
-- Name: measurementmobilehistory; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.measurementmobilehistory (
    id bigint DEFAULT nextval('intime.trafficvehiclerecordhistory_seq'::regclass) NOT NULL,
    af_1_sccm double precision,
    af_1_valid_b boolean,
    can_acc_lat_mean_mps2 double precision,
    can_acc_lat_mps2 double precision,
    can_acc_lat_var_m2ps4 double precision,
    can_acc_long_mean_mps2 double precision,
    can_acc_long_mps2 double precision,
    can_acc_long_var_m2ps4 double precision,
    can_speed_mps double precision,
    can_valid_b boolean,
    co_1_ppm double precision,
    co_1_runtime_s integer,
    co_1_valid_b boolean,
    created_on timestamp without time zone,
    gps_1_alt_m double precision,
    gps_1_hdg_deg double precision,
    gps_1_lat_deg double precision,
    gps_1_long_deg double precision,
    gps_1_pdop_nr double precision,
    gps_1_sat_nr integer,
    gps_1_speed_mps double precision,
    gps_1_valid_b boolean,
    id_driver_nr integer,
    id_runtime_s integer,
    id_status_char character varying(255),
    id_system_nr integer,
    id_vehicle_nr integer,
    id_version_char character varying(255),
    imu_acc_lat_mean_mps2 double precision,
    imu_acc_lat_mps2 double precision,
    imu_acc_lat_var_m2ps4 double precision,
    imu_acc_long_mean_mps2 double precision,
    imu_acc_long_mps2 double precision,
    imu_acc_long_var_m2ps4 double precision,
    imu_speed_mps double precision,
    imu_valid_b boolean,
    no2_1_microgm3_exp double precision,
    no2_1_microgm3_ma double precision,
    no2_1_ppb double precision,
    no2_1_runtime_s integer,
    no2_1_valid_b boolean,
    no2_2_ppb double precision,
    no2_2_runtime_s integer,
    no2_2_valid_b boolean,
    o3_1_ppb double precision,
    o3_1_runtime_s integer,
    o3_1_valid_b boolean,
    "position" public.geometry,
    realtime_delay bigint,
    res_1_a double precision,
    res_1_runtime_s integer,
    res_1_valid_b boolean,
    res_2_a double precision,
    res_2_runtime_s integer,
    res_2_valid_b boolean,
    rh_1_pct double precision,
    rh_1_valid_b boolean,
    temp_1_c double precision,
    temp_1_valid_b boolean,
    ts_ms timestamp without time zone,
    station_id bigint
);


ALTER TABLE intime.measurementmobilehistory OWNER TO bdp;

--
-- Name: measurementstring_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.measurementstring_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.measurementstring_seq OWNER TO bdp;

--
-- Name: measurementstring; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.measurementstring (
    id integer DEFAULT nextval('intime.measurementstring_seq'::regclass) NOT NULL,
    created_on timestamp without time zone,
    period integer,
    "timestamp" timestamp without time zone,
    value character varying(255),
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.measurementstring OWNER TO bdp;

--
-- Name: measurementstringhistory_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.measurementstringhistory_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.measurementstringhistory_seq OWNER TO bdp;

--
-- Name: measurementstringhistory; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.measurementstringhistory (
    id bigint DEFAULT nextval('intime.measurementstringhistory_seq'::regclass) NOT NULL,
    created_on timestamp without time zone,
    period integer,
    "timestamp" timestamp without time zone,
    value character varying(255),
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.measurementstringhistory OWNER TO bdp;

--
-- Name: meteostationbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.meteostationbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    area character varying(255),
    zeus character varying(255)
);


ALTER TABLE intime.meteostationbasicdata OWNER TO bdp;

--
-- Name: station_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.station_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.station_seq OWNER TO bdp;

--
-- Name: station; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.station (
    stationtype character varying(31) NOT NULL,
    id bigint DEFAULT nextval('intime.station_seq'::regclass) NOT NULL,
    active boolean,
    available boolean,
    description character varying(255),
    municipality character varying(255),
    name character varying(255),
    origin character varying(255),
    pointprojection public.geometry,
    shortname character varying(255),
    stationcode character varying(255) NOT NULL
);


ALTER TABLE intime.station OWNER TO bdp;

--
-- Name: streetbasicdata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.streetbasicdata (
    id bigint DEFAULT nextval('intime.basicdata_seq'::regclass) NOT NULL,
    station_id bigint,
    description character varying(255),
    length double precision,
    linegeometry public.geometry,
    old_idstr smallint,
    speed_default integer
);


ALTER TABLE intime.streetbasicdata OWNER TO bdp;

--
-- Name: translation_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.translation_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.translation_seq OWNER TO bdp;

--
-- Name: translation; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.translation (
    dtype character varying(31) NOT NULL,
    id bigint DEFAULT nextval('intime.translation_seq'::regclass) NOT NULL,
    address character varying(255),
    city character varying(255),
    name character varying(255)
);


ALTER TABLE intime.translation OWNER TO bdp;

--
-- Name: type_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.type_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.type_seq OWNER TO bdp;

--
-- Name: type; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.type (
    id bigint DEFAULT nextval('intime.type_seq'::regclass) NOT NULL,
    cname character varying(255),
    created_on timestamp without time zone,
    cunit character varying(255),
    description character varying(255),
    rtype character varying(255)
);


ALTER TABLE intime.type OWNER TO bdp;

--
-- Name: alarm alarm_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.alarm
    ADD CONSTRAINT alarm_pkey PRIMARY KEY (id);


--
-- Name: alarmspecification alarmspecification_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.alarmspecification
    ADD CONSTRAINT alarmspecification_pkey PRIMARY KEY (id);


--
-- Name: bdppermissions bdppermissions_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdppermissions
    ADD CONSTRAINT bdppermissions_pkey PRIMARY KEY (uuid);


--
-- Name: bdprole bdprole_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprole
    ADD CONSTRAINT bdprole_pkey PRIMARY KEY (id);


--
-- Name: bdprules bdprules_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprules
    ADD CONSTRAINT bdprules_pkey PRIMARY KEY (id);


--
-- Name: bdpuser bdpuser_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdpuser
    ADD CONSTRAINT bdpuser_pkey PRIMARY KEY (id);


--
-- Name: bicyclebasicdata bicyclebasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bicyclebasicdata
    ADD CONSTRAINT bicyclebasicdata_pkey PRIMARY KEY (id);


--
-- Name: bikesharingstationbasicdata bikesharingstationbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bikesharingstationbasicdata
    ADD CONSTRAINT bikesharingstationbasicdata_pkey PRIMARY KEY (id);


--
-- Name: bluetoothbasicdata bluetoothbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bluetoothbasicdata
    ADD CONSTRAINT bluetoothbasicdata_pkey PRIMARY KEY (id);


--
-- Name: carparkingbasicdata carparkingbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carparkingbasicdata
    ADD CONSTRAINT carparkingbasicdata_pkey PRIMARY KEY (id);


--
-- Name: carparkingdynamic carparkingdynamic_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carparkingdynamic
    ADD CONSTRAINT carparkingdynamic_pkey PRIMARY KEY (id);


--
-- Name: carparkingdynamichistory carparkingdynamichistory_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carparkingdynamichistory
    ADD CONSTRAINT carparkingdynamichistory_pkey PRIMARY KEY (id);


--
-- Name: carpoolinghubbasicdata carpoolinghubbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinghubbasicdata
    ADD CONSTRAINT carpoolinghubbasicdata_pkey PRIMARY KEY (id);


--
-- Name: carpoolinghubbasicdata_translation carpoolinghubbasicdata_translation_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinghubbasicdata_translation
    ADD CONSTRAINT carpoolinghubbasicdata_translation_pkey PRIMARY KEY (carpoolinghubbasicdata_id, i18n_key);


--
-- Name: carpoolinguserbasicdata carpoolinguserbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinguserbasicdata
    ADD CONSTRAINT carpoolinguserbasicdata_pkey PRIMARY KEY (id);


--
-- Name: carpoolinguserbasicdata_translation carpoolinguserbasicdata_translation_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinguserbasicdata_translation
    ADD CONSTRAINT carpoolinguserbasicdata_translation_pkey PRIMARY KEY (carpoolinguserbasicdata_id, location_key);


--
-- Name: carsharingcarstationbasicdata carsharingcarstationbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carsharingcarstationbasicdata
    ADD CONSTRAINT carsharingcarstationbasicdata_pkey PRIMARY KEY (id);


--
-- Name: carsharingstationbasicdata carsharingstationbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carsharingstationbasicdata
    ADD CONSTRAINT carsharingstationbasicdata_pkey PRIMARY KEY (id);


--
-- Name: datatype_i18n datatype_i18n_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.datatype_i18n
    ADD CONSTRAINT datatype_i18n_pkey PRIMARY KEY (datatype_id, i18n_key);


--
-- Name: echargingplugbasicdata echargingplugbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.echargingplugbasicdata
    ADD CONSTRAINT echargingplugbasicdata_pkey PRIMARY KEY (id);


--
-- Name: echargingplugoutlet echargingplugoutlet_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.echargingplugoutlet
    ADD CONSTRAINT echargingplugoutlet_pkey PRIMARY KEY (id);


--
-- Name: echargingstationbasicdata echargingstationbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.echargingstationbasicdata
    ADD CONSTRAINT echargingstationbasicdata_pkey PRIMARY KEY (id);


--
-- Name: elaboration elaboration_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.elaboration
    ADD CONSTRAINT elaboration_pkey PRIMARY KEY (id);


--
-- Name: elaborationhistory elaborationhistory_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.elaborationhistory
    ADD CONSTRAINT elaborationhistory_pkey PRIMARY KEY (id);


--
-- Name: linkbasicdata linkbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.linkbasicdata
    ADD CONSTRAINT linkbasicdata_pkey PRIMARY KEY (id);


--
-- Name: measurement measurement_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurement
    ADD CONSTRAINT measurement_pkey PRIMARY KEY (id);


--
-- Name: measurementhistory measurementhistory_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementhistory
    ADD CONSTRAINT measurementhistory_pkey PRIMARY KEY (id);


--
-- Name: measurementmobile measurementmobile_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementmobile
    ADD CONSTRAINT measurementmobile_pkey PRIMARY KEY (id);


--
-- Name: measurementmobilehistory measurementmobilehistory_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementmobilehistory
    ADD CONSTRAINT measurementmobilehistory_pkey PRIMARY KEY (id);


--
-- Name: measurementstring measurementstring_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstring
    ADD CONSTRAINT measurementstring_pkey PRIMARY KEY (id);


--
-- Name: measurementstringhistory measurementstringhistory_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstringhistory
    ADD CONSTRAINT measurementstringhistory_pkey PRIMARY KEY (id);


--
-- Name: meteostationbasicdata meteostationbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.meteostationbasicdata
    ADD CONSTRAINT meteostationbasicdata_pkey PRIMARY KEY (id);


--
-- Name: station station_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.station
    ADD CONSTRAINT station_pkey PRIMARY KEY (id);


--
-- Name: streetbasicdata streetbasicdata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.streetbasicdata
    ADD CONSTRAINT streetbasicdata_pkey PRIMARY KEY (id);


--
-- Name: translation translation_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.translation
    ADD CONSTRAINT translation_pkey PRIMARY KEY (id);


--
-- Name: type type_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.type
    ADD CONSTRAINT type_pkey PRIMARY KEY (id);


--
-- Name: measurementhistory uk46ymdfj63griskpxoou335uqn; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementhistory
    ADD CONSTRAINT uk46ymdfj63griskpxoou335uqn UNIQUE (station_id, type_id, "timestamp", period);


--
-- Name: carpoolinghubbasicdata_translation uk_3m4xdkq0ub9i16b756rmkrrub; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinghubbasicdata_translation
    ADD CONSTRAINT uk_3m4xdkq0ub9i16b756rmkrrub UNIQUE (i18n_id);


--
-- Name: bdprole uk_47fpix67ktrhok7ee98qr4h9j; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprole
    ADD CONSTRAINT uk_47fpix67ktrhok7ee98qr4h9j UNIQUE (name);


--
-- Name: measurement uk_6tvbxvtiou8witoj88k9jp48r; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurement
    ADD CONSTRAINT uk_6tvbxvtiou8witoj88k9jp48r UNIQUE (period, station_id, type_id);


--
-- Name: carpoolinguserbasicdata_translation uk_cf78jl8o9ay9e0qjys8oxbm34; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinguserbasicdata_translation
    ADD CONSTRAINT uk_cf78jl8o9ay9e0qjys8oxbm34 UNIQUE (location_id);


--
-- Name: bdpuser uk_mrv0fdjjst8g4daq6l9vj5mk4; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdpuser
    ADD CONSTRAINT uk_mrv0fdjjst8g4daq6l9vj5mk4 UNIQUE (email);


--
-- Name: station ukhfn4lbwi40pp9clr4faewciqh; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.station
    ADD CONSTRAINT ukhfn4lbwi40pp9clr4faewciqh UNIQUE (stationcode, stationtype);


--
-- Name: bdppermissions_stp_idx; Type: INDEX; Schema: intime; Owner: bdp
--

CREATE INDEX bdppermissions_stp_idx ON intime.bdppermissions USING btree (station_id, type_id, period);


--
-- Name: measurement_tsdesc_idx; Type: INDEX; Schema: intime; Owner: bdp
--

CREATE INDEX measurement_tsdesc_idx ON intime.measurement USING btree ("timestamp" DESC);


--
-- Name: measurementhistory_tsdesc_idx; Type: INDEX; Schema: intime; Owner: bdp
--

CREATE INDEX measurementhistory_tsdesc_idx ON intime.measurementhistory USING btree ("timestamp" DESC);


--
-- Name: measurementmobile fk1dnojfv99vxielbkj9vv3u9wa; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementmobile
    ADD CONSTRAINT fk1dnojfv99vxielbkj9vv3u9wa FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: bdpusers_bdproles fk20veioqa371nn7pk4r8j23hgq; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdpusers_bdproles
    ADD CONSTRAINT fk20veioqa371nn7pk4r8j23hgq FOREIGN KEY (role_id) REFERENCES intime.bdprole(id);


--
-- Name: bicyclebasicdata fk3gyvu9e5k94rx6dy5npnl8pj6; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bicyclebasicdata
    ADD CONSTRAINT fk3gyvu9e5k94rx6dy5npnl8pj6 FOREIGN KEY (bikesharingstation_id) REFERENCES intime.station(id);


--
-- Name: echargingplugbasicdata fk3rd3lm3eceksp7b9fy1fgpgq6; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.echargingplugbasicdata
    ADD CONSTRAINT fk3rd3lm3eceksp7b9fy1fgpgq6 FOREIGN KEY (estation_id) REFERENCES intime.station(id);


--
-- Name: bikesharingstationbasicdata fk4f134epnmuntunab746r66vgv; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bikesharingstationbasicdata
    ADD CONSTRAINT fk4f134epnmuntunab746r66vgv FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: bdppermissions fk4ne8r7ss0r4eufg7qx3bdcybi; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdppermissions
    ADD CONSTRAINT fk4ne8r7ss0r4eufg7qx3bdcybi FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: carsharingcarstationbasicdata fk4sskxcenm2boj6rli581ov2dc; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carsharingcarstationbasicdata
    ADD CONSTRAINT fk4sskxcenm2boj6rli581ov2dc FOREIGN KEY (carsharingstation_id) REFERENCES intime.station(id);


--
-- Name: bdppermissions fk56emtbay781h58xpfe2phfdgn; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdppermissions
    ADD CONSTRAINT fk56emtbay781h58xpfe2phfdgn FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: bdpusers_bdproles fk64udi2ccjmedklvdikvey77n2; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdpusers_bdproles
    ADD CONSTRAINT fk64udi2ccjmedklvdikvey77n2 FOREIGN KEY (user_id) REFERENCES intime.bdpuser(id);


--
-- Name: alarm fk67hmoh6okqgqcv0j0ia04jsks; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.alarm
    ADD CONSTRAINT fk67hmoh6okqgqcv0j0ia04jsks FOREIGN KEY (specification_id) REFERENCES intime.alarmspecification(id);


--
-- Name: measurementhistory fk6ft0if5pwoff43uyhh4g6mrv5; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementhistory
    ADD CONSTRAINT fk6ft0if5pwoff43uyhh4g6mrv5 FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: bdprules fk7ptqedikeqqbqmqlpspy0fny1; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprules
    ADD CONSTRAINT fk7ptqedikeqqbqmqlpspy0fny1 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: carpoolinghubbasicdata_translation fk8bg2cr872cl7bpl1xlm7gbrme; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinghubbasicdata_translation
    ADD CONSTRAINT fk8bg2cr872cl7bpl1xlm7gbrme FOREIGN KEY (i18n_id) REFERENCES intime.translation(id);


--
-- Name: measurementstringhistory fk9dpm4i4fnimhnxw80k7tfqox0; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstringhistory
    ADD CONSTRAINT fk9dpm4i4fnimhnxw80k7tfqox0 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: alarm fk9w0ph05u8x3louer75d8d6g0t; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.alarm
    ADD CONSTRAINT fk9w0ph05u8x3louer75d8d6g0t FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: echargingplugbasicdata fk_4jocxfkbgifv7nuegafam1646; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.echargingplugbasicdata
    ADD CONSTRAINT fk_4jocxfkbgifv7nuegafam1646 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: meteostationbasicdata fk_dclq4pqlin82mp08ypayegvp0; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.meteostationbasicdata
    ADD CONSTRAINT fk_dclq4pqlin82mp08ypayegvp0 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: carpoolinghubbasicdata fk_denjym4bibvsqtnj6uhh1yhnw; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinghubbasicdata
    ADD CONSTRAINT fk_denjym4bibvsqtnj6uhh1yhnw FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: carparkingbasicdata fk_du6n60nd0ltuy2l0p4byo9tno; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carparkingbasicdata
    ADD CONSTRAINT fk_du6n60nd0ltuy2l0p4byo9tno FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: bikesharingstationbasicdata fk_eyme20c34nvso7l79kmyyg8q3; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bikesharingstationbasicdata
    ADD CONSTRAINT fk_eyme20c34nvso7l79kmyyg8q3 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: carsharingcarstationbasicdata fk_gd4fjxeuhk02yg6bjgees79s6; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carsharingcarstationbasicdata
    ADD CONSTRAINT fk_gd4fjxeuhk02yg6bjgees79s6 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: linkbasicdata fk_kbxohrwokjxqvk2v2hq2h2t7q; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.linkbasicdata
    ADD CONSTRAINT fk_kbxohrwokjxqvk2v2hq2h2t7q FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: bicyclebasicdata fk_kd6cluti67bvjsnon5ane961x; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bicyclebasicdata
    ADD CONSTRAINT fk_kd6cluti67bvjsnon5ane961x FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: carpoolinguserbasicdata fk_knt96vdxugt1opmbg3l4goku7; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinguserbasicdata
    ADD CONSTRAINT fk_knt96vdxugt1opmbg3l4goku7 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: echargingstationbasicdata fk_litvoxxj0dp67mt9v0j5wg7fh; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.echargingstationbasicdata
    ADD CONSTRAINT fk_litvoxxj0dp67mt9v0j5wg7fh FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: bluetoothbasicdata fk_pyqilmrqxl9gxwpr8ui7jexij; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bluetoothbasicdata
    ADD CONSTRAINT fk_pyqilmrqxl9gxwpr8ui7jexij FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: carsharingstationbasicdata fk_qhe8k3ekrkq81hib4u039e6mr; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carsharingstationbasicdata
    ADD CONSTRAINT fk_qhe8k3ekrkq81hib4u039e6mr FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: streetbasicdata fk_qq8vbw6c0asj0wo1mycuwil93; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.streetbasicdata
    ADD CONSTRAINT fk_qq8vbw6c0asj0wo1mycuwil93 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: bdprole fka9v6xc44bmdp6ngcev8w6qxr5; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprole
    ADD CONSTRAINT fka9v6xc44bmdp6ngcev8w6qxr5 FOREIGN KEY (parent_id) REFERENCES intime.bdprole(id);


--
-- Name: elaboration fkav5dnq85ljasqix0nnjhbcoy8; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.elaboration
    ADD CONSTRAINT fkav5dnq85ljasqix0nnjhbcoy8 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: measurementmobilehistory fkdc8h2uo6mi73f6sho1cbc4qqt; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementmobilehistory
    ADD CONSTRAINT fkdc8h2uo6mi73f6sho1cbc4qqt FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: elaboration fkdi3xmx3ick2mh5tf5kga91evx; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.elaboration
    ADD CONSTRAINT fkdi3xmx3ick2mh5tf5kga91evx FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: carpoolinguserbasicdata_translation fkdq581wabl1levlfsq4pyjpjw5; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinguserbasicdata_translation
    ADD CONSTRAINT fkdq581wabl1levlfsq4pyjpjw5 FOREIGN KEY (carpoolinguserbasicdata_id) REFERENCES intime.carpoolinguserbasicdata(id);


--
-- Name: bdprules fkdten6vp3aa3r30ixmaxr0qcj1; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprules
    ADD CONSTRAINT fkdten6vp3aa3r30ixmaxr0qcj1 FOREIGN KEY (role_id) REFERENCES intime.bdprole(id);


--
-- Name: linkbasicdata fketmcfal9cr1pyv8f2kvwelm44; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.linkbasicdata
    ADD CONSTRAINT fketmcfal9cr1pyv8f2kvwelm44 FOREIGN KEY (origin_id) REFERENCES intime.station(id);


--
-- Name: bicyclebasicdata fkeu76hkyyutdvnr78grwuvd2qt; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bicyclebasicdata
    ADD CONSTRAINT fkeu76hkyyutdvnr78grwuvd2qt FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: measurementhistory fkgn083v6hfhqnguemmu0tqm1wp; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementhistory
    ADD CONSTRAINT fkgn083v6hfhqnguemmu0tqm1wp FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: elaborationhistory fkh3qi0htd1jkshh1ep377fovo4; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.elaborationhistory
    ADD CONSTRAINT fkh3qi0htd1jkshh1ep377fovo4 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: echargingplugoutlet fkhce8yoanxbbeurhseaf2pu80j; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.echargingplugoutlet
    ADD CONSTRAINT fkhce8yoanxbbeurhseaf2pu80j FOREIGN KEY (plug_id) REFERENCES intime.station(id);


--
-- Name: carpoolinguserbasicdata fkhf0s6h0u5gkbu7jn49sq62tnt; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinguserbasicdata
    ADD CONSTRAINT fkhf0s6h0u5gkbu7jn49sq62tnt FOREIGN KEY (hub_id) REFERENCES intime.station(id);


--
-- Name: measurement fkj13h3be4x0l41ge05ltnxmrax; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurement
    ADD CONSTRAINT fkj13h3be4x0l41ge05ltnxmrax FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: measurement fkjnjrf7o7u7da0nefthimj167y; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurement
    ADD CONSTRAINT fkjnjrf7o7u7da0nefthimj167y FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: measurementstringhistory fkjopbglno7qpm4sv8w43oacmur; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstringhistory
    ADD CONSTRAINT fkjopbglno7qpm4sv8w43oacmur FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: measurementstring fkkk0k0on8scofrsfmsr9o577gk; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstring
    ADD CONSTRAINT fkkk0k0on8scofrsfmsr9o577gk FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: carparkingdynamichistory fkkno932ygx9q10x5s4hdt43eu5; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carparkingdynamichistory
    ADD CONSTRAINT fkkno932ygx9q10x5s4hdt43eu5 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: datatype_i18n fkkuxk6ww2a8dxcub3iw9byny1k; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.datatype_i18n
    ADD CONSTRAINT fkkuxk6ww2a8dxcub3iw9byny1k FOREIGN KEY (datatype_id) REFERENCES intime.type(id);


--
-- Name: carpoolinghubbasicdata_translation fko7bkcktpodp3yst428317ipuk; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinghubbasicdata_translation
    ADD CONSTRAINT fko7bkcktpodp3yst428317ipuk FOREIGN KEY (carpoolinghubbasicdata_id) REFERENCES intime.carpoolinghubbasicdata(id);


--
-- Name: linkbasicdata fkorulsos70qv0goe1gpqmog7q5; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.linkbasicdata
    ADD CONSTRAINT fkorulsos70qv0goe1gpqmog7q5 FOREIGN KEY (destination_id) REFERENCES intime.station(id);


--
-- Name: measurementstring fkpi0ege52d6f86n8o6l6s75irm; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstring
    ADD CONSTRAINT fkpi0ege52d6f86n8o6l6s75irm FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: carpoolinguserbasicdata_translation fkq8p7bo9hwkfk8h3b7fhf3xflp; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carpoolinguserbasicdata_translation
    ADD CONSTRAINT fkq8p7bo9hwkfk8h3b7fhf3xflp FOREIGN KEY (location_id) REFERENCES intime.translation(id);


--
-- Name: carparkingdynamic fkq90lpabiye1scahh0pa6drni7; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.carparkingdynamic
    ADD CONSTRAINT fkq90lpabiye1scahh0pa6drni7 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: elaborationhistory fkqkj3j3kx7yctkubhil9a9kkqs; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.elaborationhistory
    ADD CONSTRAINT fkqkj3j3kx7yctkubhil9a9kkqs FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: bdprules fkqno7vwsq5oovnaxfun2sjmcc3; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprules
    ADD CONSTRAINT fkqno7vwsq5oovnaxfun2sjmcc3 FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: bdppermissions fkr9mxonrm4l54hop1l8r4pxedn; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdppermissions
    ADD CONSTRAINT fkr9mxonrm4l54hop1l8r4pxedn FOREIGN KEY (role_id) REFERENCES intime.bdprole(id);


--
-- Name: SCHEMA intime; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON SCHEMA intime TO bdp;
GRANT USAGE ON SCHEMA intime TO bdpreadonly;


--
-- PostgreSQL database dump complete
--

