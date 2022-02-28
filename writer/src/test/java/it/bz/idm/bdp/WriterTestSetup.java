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

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;

public class WriterTestSetup extends AbstractJUnit4SpringContextTests {

	/**
	 * This is the prefix for all things created inside a DB, which will
	 * also be delete, so please careful what you set here! We add it prior
	 * to any entity's natural key.
	 */
	protected static final String prefix = "--TEST--";

	protected EntityManager em;
	protected Station station;
	protected DataType type;
	protected Measurement measurement;

	@Before
	public void setup() {
		em = JPAUtil.createEntityManager();

		station = new Station(prefix + "Environment", prefix + "Station01", "Station One");
		type = new DataType(prefix + "NO2", "mg", "Fake type", "Instants");
		measurement = new Measurement(station, type, 1.11, new Date(), 500);

		try {
			em.getTransaction().begin();
			em.persist(station);
			em.persist(type);
			em.persist(measurement);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			if (em.isOpen()) {
				em.clear();
				em.close();
			}
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
			em.createQuery("UPDATE Station SET meta_data_id = NULL WHERE stationcode LIKE '" + prefix + "%'").executeUpdate();
			em.createQuery("DELETE FROM BDPRules WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + prefix + "%')").executeUpdate();
			em.createQuery("DELETE FROM MetaData WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + prefix + "%')").executeUpdate();
			em.createQuery("DELETE FROM Station WHERE stationcode LIKE '" + prefix + "%'").executeUpdate();
			em.createQuery("DELETE FROM DataType WHERE cname LIKE '" + prefix + "%'").executeUpdate();
			em.createQuery("DELETE FROM BDPRole WHERE name LIKE '" + prefix + "%'").executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			if (em.isOpen()) {
				em.clear();
				em.close();
			}
		}
	}
}
