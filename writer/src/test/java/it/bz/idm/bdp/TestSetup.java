package it.bz.idm.bdp;

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.authentication.BDPRules;
import it.bz.idm.bdp.dal.util.JPAUtil;

public class TestSetup extends AbstractJUnit4SpringContextTests {

	/**
	 * This is the prefix for all things created inside a DB, which will
	 * also be delete, so please careful what you set here! We add it prior
	 * to any entity's natural key.
	 */
	protected static final String prefix = "--TEST--";

	protected EntityManager em;
	protected BDPRole roleParent, roleChild;
	protected Station station;
	protected DataType type;
	protected BDPRules rule;
	protected Measurement measurement;

	@Before
	public void setup() {
		em = JPAUtil.createEntityManager();

		roleParent = new BDPRole(prefix + "parent", "The Parent Role");
		roleChild = new BDPRole(prefix + "child", "The Child Role", roleParent);
		station = new Station(prefix + "Environment", prefix + "Station01");
		type = new DataType(prefix + "NO2", "mg", "Fake type", "Instants");
		rule = new BDPRules();
		rule.setPeriod(500);
		rule.setRole(roleParent);
		rule.setStation(station);
		rule.setType(type);
		measurement = new Measurement(station, type, 1.11, new Date(), 500);
		measurement.setId(100L); // FIXME Manually set ID: needed for inherited classes of "M"
		try {
			em.getTransaction().begin();
			em.persist(roleParent);
			em.persist(roleChild);
			em.persist(station);
			em.persist(type);
			em.persist(measurement);
			em.persist(rule);
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
			throw e;
		}
	}

	@After
	public void cleanup() {
		/*
		 * Clean the database after tests have been run. We delete everything,
		 * that has "prefix" (see above) as natural key.
		 */
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Measurement WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + prefix + "%')").executeUpdate();
			em.createQuery("DELETE FROM Measurement WHERE type_id IN (SELECT id FROM DataType WHERE cname LIKE '" + prefix + "%')").executeUpdate();
			em.createQuery("UPDATE Station SET metadata_id = NULL WHERE stationcode LIKE '" + prefix + "%'").executeUpdate();
			em.createQuery("DELETE FROM BDPRules WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + prefix + "%')").executeUpdate();
			em.createQuery("DELETE FROM MetaData WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + prefix + "%')").executeUpdate();
			em.createQuery("DELETE FROM Station WHERE stationcode LIKE '" + prefix + "%'").executeUpdate();
			em.createQuery("DELETE FROM DataType WHERE cname LIKE '" + prefix + "%'").executeUpdate();
			em.createQuery("DELETE FROM BDPRole WHERE name LIKE '" + prefix + "%'").executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (em.isOpen())
				em.close();
		}
	}
}
