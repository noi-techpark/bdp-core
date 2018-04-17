package it.bz.idm.bdp;

import static org.junit.Assert.assertNotNull;

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
import it.bz.idm.bdp.dal.authentication.BDPPermission;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.environment.Environmentstation;
import it.bz.idm.bdp.dal.util.JPAUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/META-INF/spring/applicationContext.xml"})
public class SecurityIT extends AbstractJUnit4SpringContextTests{
	private static EntityManager entityManager;
	private BDPRole role,role2;
	private Station station;
	private DataType type;
	private BDPPermission permission;
	
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
		permission = new BDPPermission();
		permission.setPeriod(500);
		permission.setRole(role);
		permission.setStation(station);
		permission.setType(type);

	}
	
	@Test
	public void testJPAUtilInit() {
		assertNotNull(entityManager);
	}

	@Test
	public void testGetRulesForRole() {
		BDPRole r = BDPRole.findByName(entityManager, role.getName());
		TypedQuery<Measurement> query = entityManager.createQuery(
				"select m from Measurement m join BDPRules ru where ru.role = :role and (m.station = ru.station or ru.station = null)"
				+ " and (m.type = ru.type or ru.type = null) and (m.period = ru.period or ru.period = null)",
				Measurement.class);
		query.setParameter("role", r);
		List<Measurement> resultList = query.getResultList();
		assertNotNull(resultList);
		
	}
	
	@Test
	public void testInsertPermission() {
	}
	
	@AfterClass
	public static void cleanup() {
	}
}
