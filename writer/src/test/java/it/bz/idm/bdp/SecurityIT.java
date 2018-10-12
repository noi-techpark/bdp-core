/**
 * Big data platform - Data Writer for the Big Data Platform, that writes changes to the database
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
package it.bz.idm.bdp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
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
import it.bz.idm.bdp.dal.environment.EnvironmentStation;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.writer.DataManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/META-INF/spring/applicationContext.xml"})
public class SecurityIT extends AbstractJUnit4SpringContextTests{
	private EntityManager em;
	private BDPRole role,role2;
	private Station station;
	private DataType type;
	private BDPRules rule;
	private Measurement measurement;
	private static boolean firstrun = true;

	@Before
	public void setup() {
		em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		role = new BDPRole();
		role.setName("Holla");
		role.setDescription("The Parent Role");
		role2 = new BDPRole();
		role2.setName("Halla");
		role2.setDescription("The Child Role");
		role2.setParent(role);
		station= new EnvironmentStation("BLuesky");
		type = new DataType("NO2","mg","Fake type","Instants");
		rule = new BDPRules();
		rule.setPeriod(500);
		rule.setRole(role);
		rule.setStation(station);
		rule.setType(type);
		measurement = new Measurement(station, type, 1.11, new Date(), 500);

		if (firstrun) {
			em.persist(role);
			em.persist(role2);
			em.persist(station);
			em.persist(type);
			em.persist(measurement);
			firstrun = false;
		}
	}

	@Test
	public void testInitialData() {
		/* Admin and guest roles must always be present */
		BDPRole admin = BDPRole.fetchAdminRole(em);
		assertTrue(BDPRole.ROLE_ADMIN.equals(admin.getName()));
		BDPRole guest = BDPRole.fetchGuestRole(em);
		assertTrue(BDPRole.ROLE_GUEST.equals(guest.getName()));

		/* Admin roles must have a rule to see everything */
		TypedQuery<BDPRules> query = em.createQuery("select r from BDPRules r where r.role = :role", BDPRules.class);
		query.setParameter("role", admin);
		BDPRules rule = JPAUtil.getSingleResultOrNull(query);
		assertNotNull(rule);
		assertNull(rule.getPeriod());
		assertNull(rule.getStation());
		assertNull(rule.getType());
	}

	@Test
	public void testJPAUtilInit() {
		assertNotNull(em);
	}

	@Test
	public void testGetRulesForRole() {
		BDPRole r = BDPRole.findByName(em, role.getName());
		//JOIN between unrelated entities https://www.thoughts-on-java.org/how-to-join-unrelated-entities/
		//had to upgrade hibernate to resolve this known issue https://hibernate.atlassian.net/browse/HHH-2772
		TypedQuery<Measurement> query = em.createQuery(
				"select m from Measurement m join BDPPermissions ru on (m.station = ru.station or ru.station = null)"
				+ " and (m.type = ru.type or ru.type = null) and (m.period = ru.period or ru.period = null) where ru.role = :role",
				Measurement.class);
		query.setParameter("role", r);
		List<Measurement> resultList = query.getResultList();
		assertNotNull(resultList);

	}

	@Test
	public void testRulesForLastRecord() {
		BDPRole r = BDPRole.findByName(em, role.getName());
		BDPRole r2 = BDPRole.fetchAdminRole(em);
		assertNotNull(r);
		assertNotNull(r2);
		Station station = new EnvironmentStation().findStation(em, this.station.getName());
		assertNotNull(station);
		DataType type = DataType.findByCname(em, this.type.getCname());
		assertNotNull(type);
		Measurement m = Measurement.findLatestEntry(em, station, null, null, r2);
		assertNotNull(m);
		Measurement m2 = Measurement.findLatestEntry(em, station, null, null, r);
		assertNull(m2);
	}

	@Test
	public void testSyncStations() {
		StationDto s = new StationDto("!TEST-WRITER", null, null, null);
		DataManager m = new DataManager();
		List<StationDto> dtos = new ArrayList<StationDto>();
		dtos.add(s);
		m.syncStations("EnvironmentStation", dtos);
	}

	@Test
	public void testSyncDataTypes() {
		DataTypeDto t = new DataTypeDto("!TEST-WRITER", null, null, null);
		DataManager m = new DataManager();
		List<DataTypeDto> dtos = new ArrayList<DataTypeDto>();
		dtos.add(t);
		m.syncDataTypes(dtos);
	}

	@After
	public void cleanup() {
		em.getTransaction().rollback();
		em.close();
	}
}
