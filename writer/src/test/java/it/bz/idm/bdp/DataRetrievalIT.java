/**
 * writer - Data Writer for the Big Data Platform
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
package it.bz.idm.bdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.MeasurementAbstract;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.EventDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.writer.DataManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/META-INF/spring/applicationContext.xml"})
public class DataRetrievalIT extends WriterTestSetup {

	@Test
	public void testStationFetch() {
		Station station = Station.findStation(em, prefix + "non-existent-stationtype", prefix + "hey");
		assertNull(station);
		List<Station> stationsWithOrigin = Station.findStations(em, prefix + "TrafficSensor", prefix + "FAMAS-traffic");
		assertNotNull(stationsWithOrigin);
		List<Station> stations = Station.findStations(em, prefix + "TrafficSensor", null);
		assertNotNull(stations);
	}

	@Test
	public void testFindLatestEntry() {
		BDPRole role = BDPRole.fetchAdminRole(em);
		Integer period = 500;
		DataType type = DataType.findByCname(em, this.type.getCname());
		Station station = Station.findStation(em, this.station.getStationtype(), this.station.getStationcode());
		MeasurementAbstract latestEntry = new Measurement().findLatestEntry(em, station, type, period, role);
		assertNotNull(latestEntry);
		assertEquals(period, latestEntry.getPeriod());
		assertTrue(this.station.getActive());
		assertTrue(this.station.getAvailable());
	}

	@Test
	public void testSyncStations() {
		StationDto s = new StationDto(prefix + "WRITER", "Some name", null, null);
		List<StationDto> dtos = new ArrayList<StationDto>();
		dtos.add(s);
		DataManager.syncStations(prefix + "EnvironmentStation", dtos, null); // TODO Update response location
	}

	@Test
	public void testSyncDataTypes() {
		DataTypeDto t = new DataTypeDto(prefix + "WRITER", null, null, null);
		List<DataTypeDto> dtos = new ArrayList<DataTypeDto>();
		dtos.add(t);
		DataManager.syncDataTypes(dtos, null);
	}
	@Test
	public void testAddEvents() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

		EventDto t = new EventDto();
		t.setId(UUID.randomUUID().toString());
		t.setCategory("category");
		t.setDescription("description");
		t.setEventEnd(new Date().getTime());
		t.setEventStart(new Date().getTime());
		Coordinate coordinate = new Coordinate(45., 11.);
		t.setGeoJson(geometryFactory.createPoint(coordinate));
		t.setLocationDescription("Fake location");
		Map<String, Object> metaData = new HashMap<>();
		metaData.put("test", 5);
		t.setMetaData(metaData );
		t.setOrigin("origin");
		List<EventDto> dtos = new ArrayList<EventDto>();
		dtos.add(t);
		DataManager.addEvents(dtos, null);
	}
}
