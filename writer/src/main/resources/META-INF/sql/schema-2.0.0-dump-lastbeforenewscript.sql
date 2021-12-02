--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.15
-- Dumped by pg_dump version 11.1 (Ubuntu 11.1-1.pgdg18.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: intime; Type: SCHEMA; Schema: -; Owner: bdp
--

CREATE SCHEMA intime;


ALTER SCHEMA intime OWNER TO bdp;

SET default_tablespace = '';

SET default_with_oids = false;

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
-- Name: edge_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.edge_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.edge_seq OWNER TO bdp;

--
-- Name: edge; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.edge (
    id bigint DEFAULT nextval('intime.edge_seq'::regclass) NOT NULL,
    directed boolean DEFAULT true NOT NULL,
    linegeometry public.geometry,
    destination_id bigint NOT NULL,
    edgedata_id bigint,
    origin_id bigint NOT NULL
);


ALTER TABLE intime.edge OWNER TO bdp;

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
    created_on timestamp without time zone NOT NULL,
    period integer NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    doublevalue double precision,
    provenance_id bigint,
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
    doublevalue double precision,
    provenance_id bigint,
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.measurementhistory OWNER TO bdp;

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
    id bigint DEFAULT nextval('intime.measurementstring_seq'::regclass) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    period integer NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    stringvalue character varying(255),
    provenance_id bigint,
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
    stringvalue character varying(255),
    provenance_id bigint,
    station_id bigint,
    type_id bigint
);


ALTER TABLE intime.measurementstringhistory OWNER TO bdp;

--
-- Name: metadata_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.metadata_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.metadata_seq OWNER TO bdp;

--
-- Name: metadata; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.metadata (
    id bigint DEFAULT nextval('intime.metadata_seq'::regclass) NOT NULL,
    created_on timestamp without time zone,
    json jsonb,
    station_id bigint
);


ALTER TABLE intime.metadata OWNER TO bdp;

--
-- Name: provenance_seq; Type: SEQUENCE; Schema: intime; Owner: bdp
--

CREATE SEQUENCE intime.provenance_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intime.provenance_seq OWNER TO bdp;

--
-- Name: provenance; Type: TABLE; Schema: intime; Owner: bdp
--

CREATE TABLE intime.provenance (
    id bigint DEFAULT nextval('intime.provenance_seq'::regclass) NOT NULL,
    datacollector character varying(255) NOT NULL,
    datacollectorversion character varying(255),
    lineage character varying(255) NOT NULL
);


ALTER TABLE intime.provenance OWNER TO bdp;

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
    id bigint DEFAULT nextval('intime.station_seq'::regclass) NOT NULL,
    active boolean,
    available boolean,
    name character varying(255) NOT NULL,
    origin character varying(255),
    pointprojection public.geometry,
    stationcode character varying(255) NOT NULL,
    stationtype character varying(255) NOT NULL,
    metadata_id bigint,
    parent_id bigint
);


ALTER TABLE intime.station OWNER TO bdp;

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
    cname character varying(255) NOT NULL,
    created_on timestamp without time zone,
    cunit character varying(255),
    description character varying(255),
    rtype character varying(255)
);


ALTER TABLE intime.type OWNER TO bdp;

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
-- Name: edge edge_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.edge
    ADD CONSTRAINT edge_pkey PRIMARY KEY (id);


--
-- Name: provenance idx_provenance_l_dc_dcv; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.provenance
    ADD CONSTRAINT idx_provenance_l_dc_dcv UNIQUE (lineage, datacollector, datacollectorversion);


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
-- Name: metadata metadata_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.metadata
    ADD CONSTRAINT metadata_pkey PRIMARY KEY (id);


--
-- Name: provenance provenance_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.provenance
    ADD CONSTRAINT provenance_pkey PRIMARY KEY (id);


--
-- Name: station station_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.station
    ADD CONSTRAINT station_pkey PRIMARY KEY (id);


--
-- Name: type type_pkey; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.type
    ADD CONSTRAINT type_pkey PRIMARY KEY (id);


--
-- Name: bdprole uk_47fpix67ktrhok7ee98qr4h9j; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprole
    ADD CONSTRAINT uk_47fpix67ktrhok7ee98qr4h9j UNIQUE (name);


--
-- Name: type uk_7jg7156wbi7gvwgs6s46yf43a; Type: CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.type
    ADD CONSTRAINT uk_7jg7156wbi7gvwgs6s46yf43a UNIQUE (cname);


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
-- Name: bdpusers_bdproles fk20veioqa371nn7pk4r8j23hgq; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdpusers_bdproles
    ADD CONSTRAINT fk20veioqa371nn7pk4r8j23hgq FOREIGN KEY (role_id) REFERENCES intime.bdprole(id);


--
-- Name: measurement fk2l14xdjteupo8d5nie9x7c86k; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurement
    ADD CONSTRAINT fk2l14xdjteupo8d5nie9x7c86k FOREIGN KEY (provenance_id) REFERENCES intime.provenance(id);


--
-- Name: bdppermissions fk4ne8r7ss0r4eufg7qx3bdcybi; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdppermissions
    ADD CONSTRAINT fk4ne8r7ss0r4eufg7qx3bdcybi FOREIGN KEY (type_id) REFERENCES intime.type(id);


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
-- Name: measurementstring fk66wukreiy92ullugp5b5pooay; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstring
    ADD CONSTRAINT fk66wukreiy92ullugp5b5pooay FOREIGN KEY (provenance_id) REFERENCES intime.provenance(id);


--
-- Name: measurementhistory fk6ft0if5pwoff43uyhh4g6mrv5; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementhistory
    ADD CONSTRAINT fk6ft0if5pwoff43uyhh4g6mrv5 FOREIGN KEY (type_id) REFERENCES intime.type(id);


--
-- Name: metadata fk7g1l7gs66xvo687ar19utlyp9; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.metadata
    ADD CONSTRAINT fk7g1l7gs66xvo687ar19utlyp9 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: bdprules fk7ptqedikeqqbqmqlpspy0fny1; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprules
    ADD CONSTRAINT fk7ptqedikeqqbqmqlpspy0fny1 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: measurementhistory fk8a185rsn3lwvbbk6ylbiovjty; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementhistory
    ADD CONSTRAINT fk8a185rsn3lwvbbk6ylbiovjty FOREIGN KEY (provenance_id) REFERENCES intime.provenance(id);


--
-- Name: measurementstringhistory fk9dpm4i4fnimhnxw80k7tfqox0; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstringhistory
    ADD CONSTRAINT fk9dpm4i4fnimhnxw80k7tfqox0 FOREIGN KEY (station_id) REFERENCES intime.station(id);


--
-- Name: bdprole fka9v6xc44bmdp6ngcev8w6qxr5; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprole
    ADD CONSTRAINT fka9v6xc44bmdp6ngcev8w6qxr5 FOREIGN KEY (parent_id) REFERENCES intime.bdprole(id);


--
-- Name: edge fkbhk3m6i8hpuvw5f5wmob7lv0x; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.edge
    ADD CONSTRAINT fkbhk3m6i8hpuvw5f5wmob7lv0x FOREIGN KEY (origin_id) REFERENCES intime.station(id);


--
-- Name: bdprules fkdten6vp3aa3r30ixmaxr0qcj1; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.bdprules
    ADD CONSTRAINT fkdten6vp3aa3r30ixmaxr0qcj1 FOREIGN KEY (role_id) REFERENCES intime.bdprole(id);


--
-- Name: measurementhistory fkgn083v6hfhqnguemmu0tqm1wp; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementhistory
    ADD CONSTRAINT fkgn083v6hfhqnguemmu0tqm1wp FOREIGN KEY (station_id) REFERENCES intime.station(id);


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
-- Name: station fkl75nk4972jo3defhqu6l23o9j; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.station
    ADD CONSTRAINT fkl75nk4972jo3defhqu6l23o9j FOREIGN KEY (metadata_id) REFERENCES intime.metadata(id);


--
-- Name: edge fklo9f6f70icnhbsy5fw5vm8q87; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.edge
    ADD CONSTRAINT fklo9f6f70icnhbsy5fw5vm8q87 FOREIGN KEY (destination_id) REFERENCES intime.station(id);


--
-- Name: measurementstringhistory fkn9wb0w7f39jr7sp9bq7gh34of; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstringhistory
    ADD CONSTRAINT fkn9wb0w7f39jr7sp9bq7gh34of FOREIGN KEY (provenance_id) REFERENCES intime.provenance(id);


--
-- Name: measurementstring fkpi0ege52d6f86n8o6l6s75irm; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.measurementstring
    ADD CONSTRAINT fkpi0ege52d6f86n8o6l6s75irm FOREIGN KEY (station_id) REFERENCES intime.station(id);


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
-- Name: station fkrwkpfeoxfhn1rks97k6wlanpk; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.station
    ADD CONSTRAINT fkrwkpfeoxfhn1rks97k6wlanpk FOREIGN KEY (parent_id) REFERENCES intime.station(id);


--
-- Name: edge fks5ux522idfw9aredb1uwlq4tt; Type: FK CONSTRAINT; Schema: intime; Owner: bdp
--

ALTER TABLE ONLY intime.edge
    ADD CONSTRAINT fks5ux522idfw9aredb1uwlq4tt FOREIGN KEY (edgedata_id) REFERENCES intime.station(id);


--
-- Name: SCHEMA intime; Type: ACL; Schema: -; Owner: bdp
--

REVOKE ALL ON SCHEMA intime FROM PUBLIC;
REVOKE ALL ON SCHEMA intime FROM bdp;
GRANT ALL ON SCHEMA intime TO bdp;

GRANT USAGE ON SCHEMA intime TO bdp_readonly;


--
-- PostgreSQL database dump complete
--

