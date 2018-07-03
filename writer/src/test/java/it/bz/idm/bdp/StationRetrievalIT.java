package it.bz.idm.bdp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.writer.DataManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/META-INF/spring/applicationContext.xml"})
public class StationRetrievalIT {
	
	private EntityManager em = JPAUtil.createEntityManager();
	
	private DataManager manager = new DataManager();
	private String type = "TrafficSensor";
	private String origin = "FAMAS-traffic";

	@Test
	public void testStationsRetrieval() {
		List<Station> stationsWithOrigin = Station.findStations(em, type, origin );
		assertNotNull(stationsWithOrigin);
		List<Station> stations = Station.findStations(em, type, null);
		assertNotNull(stations);
	}
	
	@Test
	public void testDataManagerStationRetrival() {
		List<StationDto> stationDtos = manager.getStations(type, origin);
		assertNotNull(stationDtos);
		if (!stationDtos.isEmpty()) {
			assertTrue(stationDtos.get(0).getOrigin().equals(origin));
		}
	}

}
