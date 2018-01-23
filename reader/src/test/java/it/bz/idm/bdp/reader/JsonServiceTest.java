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

import it.bz.idm.bdp.dto.MeteoStationDto;
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
		when(controller.getStations(RANDOM_STATION_TYPE)).thenReturn(new ArrayList<String>() {{add("hi");add("42");add("Im a unique identifier");}});
		List<String> stations = controller.getStations(RANDOM_STATION_TYPE);
		assertNotNull(stations);
		assertFalse(stations.isEmpty());
		assertEquals("42",stations.get(1));
	}
	@Test
	public void testStationsDetailsFetch() {
		MeteoStationDto dto = new MeteoStationDto("234r4", "My invention dto",15858.12 , 0);
		dto.setArea("An area");
		MeteoStationDto anotherDto = new MeteoStationDto("234r1", null,15858.12 , 12);

		List<StationDto> list = new ArrayList<StationDto>() {{add(dto);add(anotherDto);}};
		when(controller.getStationDetails(RANDOM_STATION_TYPE,null)).then(new Returns(list));
		List<? extends StationDto> stationDetails = controller.getStationDetails(RANDOM_STATION_TYPE,null);
		assertNotNull(stationDetails);
		assertFalse(stationDetails.isEmpty());
		StationDto object = stationDetails.get(0);
		assertNotNull(object);
		assertEquals("My invention dto",object.getName());
	}

}
