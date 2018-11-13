/**
 * reader - Data Reader for the Big Data Platform, that queries the database for web-services
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
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
package it.bz.idm.bdp.reader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.runners.MockitoJUnitRunner;

import it.bz.idm.bdp.dto.StationDto;

@RunWith(MockitoJUnitRunner.class)
public class JsonServiceTest {

	private static final String RANDOM_STATION_TYPE = "Meteostation";
	private JsonController controller;

	@Before
	public void setup() {
		controller = mock(JsonController.class);
	}
	@Test
	public void testStationsFetch() {
		when(controller.getStations(RANDOM_STATION_TYPE)).thenReturn(new ArrayList<String>() {
			private static final long serialVersionUID = -1187950189580524534L;
			{
				add("hi");
				add("42");
				add("Im a unique identifier");
			}
		});
		List<String> stations = controller.getStations(RANDOM_STATION_TYPE);
		assertNotNull(stations);
		assertFalse(stations.isEmpty());
		assertEquals("42",stations.get(1));
	}
	@Test
	public void testStationsDetailsFetch() {
		StationDto dto = new StationDto("234r4", "My invention dto",15858.12 , 0.);
		dto.getMetaData().put("area","An area");
		StationDto anotherDto = new StationDto("234r1", null,15858.12 , 12.);

		List<StationDto> list = new ArrayList<StationDto>() {
			private static final long serialVersionUID = 2145736428612955944L;
			{
				add(dto);
				add(anotherDto);
			}
		};
		when(controller.getStationDetails(RANDOM_STATION_TYPE,null)).then(new Returns(list));
		List<? extends StationDto> stationDetails = controller.getStationDetails(RANDOM_STATION_TYPE,null);
		assertNotNull(stationDetails);
		assertFalse(stationDetails.isEmpty());
		StationDto object = stationDetails.get(0);
		assertNotNull(object);
		assertEquals("My invention dto",object.getName());
	}

}
