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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.MeasurementAbstract;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.EventDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.writer.DataManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = "it.bz.idm.bdp")
@Component
@WebAppConfiguration
@TestPropertySource(properties = {
	"spring.flyway.enabled=false"
})
public class DataRetrievalIT extends WriterTestSetup {

	@Autowired
	DataManager dataManager;

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
		Integer period = 500;
		DataType type = DataType.findByCname(em, this.type.getCname());
		Station station = Station.findStation(em, this.station.getStationtype(), this.station.getStationcode());
		MeasurementAbstract latestEntry = new Measurement().findLatestEntry(em, station, type, period);
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
		dataManager.syncStations(prefix + "EnvironmentStation", dtos, null, "testProvenance", "testProvenanceVersion", true); // TODO Update response location
	}

	@Test
	public void testSyncDataTypes() {
		DataTypeDto t = new DataTypeDto(prefix + "WRITER", null, null, null);
		List<DataTypeDto> dtos = new ArrayList<DataTypeDto>();
		dtos.add(t);
		dataManager.syncDataTypes(dtos, null);
	}

	@Test
	public void testAddEvents() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

		EventDto t = new EventDto();
		t.setUuid(UUID.randomUUID().toString());
		t.setCategory("category");
		t.setDescription("description");
		t.setEventEnd(new Date().getTime());
		t.setEventStart(new Date().getTime());
		Coordinate coordinate = new Coordinate(45., 11.);
		t.setWktGeometry(geometryFactory.createPoint(coordinate).toText());
		t.setLocationDescription("Fake location");
		Map<String, Object> metaData = new HashMap<>();
		metaData.put("test", 5);
		t.setMetaData(metaData );
		t.setOrigin("origin");
		t.setEventSeriesUuid(UUID.randomUUID().toString());
		t.setName("some-event-name");
		t.setProvenance("12345678");
		List<EventDto> dtos = new ArrayList<>();
		dtos.add(t);
		dataManager.addEvents(dtos, null);
	}
}
