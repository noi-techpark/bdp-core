/**
 * writer - Data Writer for the Big Data Platform
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.M;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.authentication.BDPRules;
import it.bz.idm.bdp.dal.util.JPAUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/META-INF/spring/applicationContext.xml"})
public class SecurityIT extends WriterTestSetup {

	@Test
	public void testInitialData() {
		/* Admin and guest roles must always be present */
		BDPRole admin = BDPRole.fetchAdminRole(em);
		assertTrue(BDPRole.ROLE_ADMIN.equals(admin.getName()));
		BDPRole guest = BDPRole.fetchGuestRole(em);
		assertTrue(BDPRole.ROLE_GUEST.equals(guest.getName()));

		/* Admin roles must have a rule to see everything */
		TypedQuery<BDPRules> query = em.createQuery("select r from BDPRules r where r.role = :role", BDPRules.class)
									   .setParameter("role", admin);
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
		BDPRole r = BDPRole.findByName(em, roleParent.getName());
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
		BDPRole role1 = BDPRole.findByName(em, roleParent.getName());
		BDPRole role2 = BDPRole.fetchAdminRole(em);
		assertNotNull(role1);
		assertNotNull(role2);
		Station station = Station.findStation(em, this.station.getStationtype(), this.station.getStationcode());
		assertNotNull(station);
		DataType type = DataType.findByCname(em, this.type.getCname());
		assertNotNull(type);
		M m = new Measurement().findLatestEntry(em, station, null, null, role2);
		assertNotNull(m);
		M m2 = new Measurement().findLatestEntry(em, station, null, null, role1);
		assertNull(m2);
	}

	@Test
	public void testAdminRole() {
		BDPRole role = BDPRole.fetchAdminRole(em);
		assertNotNull(role);
		assertFalse(role.getUsers()==null || role.getUsers().isEmpty());
	}

}
