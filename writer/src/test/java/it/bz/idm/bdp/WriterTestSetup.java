// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.Provenance;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.writer.DataManager;
import it.bz.idm.bdp.writer.config.PersistenceConfig;

/**
 * Setup of the writer test cases with initial data, that will be added and
 * removed for each test.
 *
 * Abstract, because we do not want to run this class itself.
 */
@Import(PersistenceConfig.class)
public abstract class WriterTestSetup extends AbstractJUnit4SpringContextTests {

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	DataManager dataManager;

	/**
	 * This is the prefix for all things created inside a DB, which will
	 * also be delete, so please careful what you set here! We add it prior
	 * to any entity's natural key.
	 */
	protected static final String PREFIX = "--TEST--";
	protected static final String STATION_TYPE = PREFIX + "Environment";

	protected EntityManager em;
	protected Station station;
	protected DataType type;
	protected Measurement measurement;
	protected Measurement measurementOld;
	protected Provenance provenance;

	@Before
	public void setup() {

		// Make sure no remainders left after last run
		cleanup();

		em = entityManagerFactory.createEntityManager();

		station = new Station(STATION_TYPE, PREFIX + "Station01", "Station One");
		type = new DataType(PREFIX + "NO2", "mg", "Fake type", "Instants");
		Date today = new Date();
		measurement = new Measurement(station, type, 1.11, today, 500);
		measurementOld = new Measurement(station, type, 2.22, new Date(today.getTime() - 1000), 500);
		provenance = new Provenance();
		provenance.setDataCollector("writer-integration-tests");
		provenance.setDataCollectorVersion("0.0.0");
		provenance.setLineage("from-the-writer-integration-tests");
		provenance.setUuid("12345678");

		List<RecordDtoImpl> values = new ArrayList<>();
		values.add(new SimpleRecordDto(measurement.getTimestamp().getTime(), measurement.getValue(), measurement.getPeriod()));
		values.add(new SimpleRecordDto(measurementOld.getTimestamp().getTime(), measurementOld.getValue(), measurementOld.getPeriod()));

		try {
			em.getTransaction().begin();
			em.persist(station);
			em.persist(type);
			em.persist(provenance);
			em.getTransaction().commit();
			dataManager.pushRecords(
				STATION_TYPE,
				null,
				DataMapDto.build(provenance.getUuid(), station.getStationcode(), type.getCname(), values)
			);
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
		em = entityManagerFactory.createEntityManager();
		try {
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Measurement WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM Measurement WHERE type_id IN (SELECT id FROM DataType WHERE cname LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementHistory WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementHistory WHERE type_id IN (SELECT id FROM DataType WHERE cname LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementString WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementString WHERE type_id IN (SELECT id FROM DataType WHERE cname LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementStringHistory WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementStringHistory WHERE type_id IN (SELECT id FROM DataType WHERE cname LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementJSON WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementJSON WHERE type_id IN (SELECT id FROM DataType WHERE cname LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementJSONHistory WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM MeasurementJSONHistory WHERE type_id IN (SELECT id FROM DataType WHERE cname LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("UPDATE Station SET meta_data_id = NULL WHERE stationcode LIKE '" + PREFIX + "%'").executeUpdate();
			em.createQuery("DELETE FROM MetaData WHERE station_id IN (SELECT id FROM Station WHERE stationcode LIKE '" + PREFIX + "%')").executeUpdate();
			em.createQuery("DELETE FROM Station WHERE stationcode LIKE '" + PREFIX + "%'").executeUpdate();
			em.createQuery("DELETE FROM DataType WHERE cname LIKE '" + PREFIX + "%'").executeUpdate();
			em.createQuery("DELETE FROM Event WHERE provenance_id = (select id from Provenance where data_collector = 'writer-integration-tests')").executeUpdate();
			em.createQuery("DELETE FROM Provenance WHERE data_collector = 'writer-integration-tests'").executeUpdate();
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
