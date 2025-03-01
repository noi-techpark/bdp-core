// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.writer.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import com.opendatahub.timeseries.bdp.writer.writer.Application;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

@DataJpaTest
@ContextConfiguration(classes = Application.class)
public class PersistenceIT {

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	private Map<String, Object> map = new HashMap<>();

	@BeforeEach
	public void setup() {
		map.put("He", Integer.valueOf(4));
	}

	@Test
	public void testSyncTypes() {
		EntityManager em = entityManagerFactory.createEntityManager();
		DataType type = new DataType("customType-2", "customUnit", "customDescription", "customRType");
		DataTypeMetaData metadata = new DataTypeMetaData(type, map);
		type.setMetaData(metadata);
		type.getMetaDataHistory().add(metadata);
		em.persist(type);
		List<DataType> types = new ArrayList<DataType>();
		types.add(type);
	}
}
