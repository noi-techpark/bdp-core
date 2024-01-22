// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.timeseries.bdp.writer.writer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.opendatahub.timeseries.bdp.writer.dal.util.PropertiesWithEnv;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Configuration
public class PersistenceConfig {

    public PersistenceConfig() {
        super();
    }

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		try {
			PropertiesWithEnv properties = PropertiesWithEnv.fromActiveSpringProfile();
			return Persistence.createEntityManagerFactory(
					"jpa-persistence", // This must correspond to the persistence.xml persistence-unit tag
					properties.getStringMap()
				);
		} catch (Exception ex) {
			throw new RuntimeException("PersistenceConfig: Cannot create EntityManagerFactory", ex);
		}
	}
}
