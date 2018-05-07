package it.bz.idm.bdp;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.authentication.BDPRules;
import it.bz.idm.bdp.dal.environment.Environmentstation;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.writer.DataManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/META-INF/spring/applicationContext.xml"})
public class SecurityIT extends AbstractJUnit4SpringContextTests{
	private static EntityManager entityManager;
	private BDPRole role,role2;
	private Station station;
	private DataType type;
	private BDPRules rule;

	@PostConstruct
	public void setup() {
		entityManager = JPAUtil.createEntityManager();
		entityManager.getTransaction().begin();
		role = new BDPRole();
		role.setName("Holla");
		role.setDescription("The Parent Role");
		role2 = new BDPRole();
		role2.setName("Halla");
		role2.setDescription("The Child Role");
		role2.setParent(role);
		station= new Environmentstation("BLuesky");
		type = new DataType("NO2","mg","Fake type","Instants");
		rule = new BDPRules();
		rule.setPeriod(500);
		rule.setRole(role);
		rule.setStation(station);
		rule.setType(type);
	}

	@Test
	public void testJPAUtilInit() {
		assertNotNull(entityManager);
	}

	@Test
	public void testGetRulesForRole() {
		BDPRole r = BDPRole.findByName(entityManager, role.getName());
		//JOIN between unrelated entities https://www.thoughts-on-java.org/how-to-join-unrelated-entities/
		//had to upgrade hibernate to resolve this known issue https://hibernate.atlassian.net/browse/HHH-2772
		TypedQuery<Measurement> query = entityManager.createQuery(
				"select m from Measurement m join BDPPermissions ru on (m.station = ru.station or ru.station = null)"
				+ " and (m.type = ru.type or ru.type = null) and (m.period = ru.period or ru.period = null) where ru.role = :role",
				Measurement.class);
		query.setParameter("role", r);
		List<Measurement> resultList = query.getResultList();
		assertNotNull(resultList);

	}

	@Test
	public void testRulesForLastRecord() {
		BDPRole r = BDPRole.findByName(entityManager, role.getName());
		Station station = new Environmentstation().findStation(entityManager, this.station.getName());
		DataType type = DataType.findByCname(entityManager, this.type.getCname());
		Measurement m = Measurement.findLatestEntry(entityManager, station, type, null, r);
		assertNotNull(m);
	}

	@Test
	public void testSyncStations() {
		StationDto s = new StationDto("!TEST-WRITER", null, null, null);
		DataManager m = new DataManager();
		List<StationDto> dtos = new ArrayList<StationDto>();
		dtos.add(s);
		m.syncStations("Environmentstation", dtos);
	}

	@Test
	public void testSyncDataTypes() {
		DataTypeDto t = new DataTypeDto("!TEST-WRITER", null, null, null);
		DataManager m = new DataManager();
		List<DataTypeDto> dtos = new ArrayList<DataTypeDto>();
		dtos.add(t);
		m.syncDataTypes(dtos);
	}

	@AfterClass
	public static void cleanup() {
	}
}
