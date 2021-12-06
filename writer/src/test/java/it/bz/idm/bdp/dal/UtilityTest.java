/**
 * BDP data - Data Access Layer for the Big Data Platform
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
package it.bz.idm.bdp.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import it.bz.idm.bdp.dal.util.PropertiesWithEnv;
import it.bz.idm.bdp.dto.EventDto;

public class UtilityTest {

	@Test
	public void testPropertiesWithEnv() {
		PropertiesWithEnv prop = new PropertiesWithEnv();
		prop.setProperty("a.test", "${__TEST_ENV_VAR}");
		try {
			prop.substitueEnv();
			fail("We expect an IllegalArgumentException!");
		} catch (IllegalArgumentException ex) {
			/* We expect this */
		}

		prop = new PropertiesWithEnv();
		prop.setProperty("a.test", "${__TEST_ENV_VAR:default-if-missing}");
		prop.substitueEnv();
		assertEquals("default-if-missing", prop.getProperty("a.test"));

		prop = new PropertiesWithEnv();
		prop.setProperty("a.test", "${__TEST_ENV_VAR:-default-if-missing}");
		prop.addEnv("__TEST_ENV_VAR", "this-is-a-test");
		prop.substitueEnv();
		assertEquals("this-is-a-test", prop.getProperty("a.test"));
	}

	@Test
	public void testRangeCasting() {
		EventDto dto = new EventDto();
		LocalDateTime startTime = LocalDateTime.of(2021, 5, 12, 4, 20);
		LocalDateTime endTime = LocalDateTime.of(2021, 5, 12, 6, 20);
		dto.setEventStart(startTime.atZone(ZoneId.systemDefault()).toEpochSecond()*1000);
		dto.setEventEnd(endTime.atZone(ZoneId.systemDefault()).toEpochSecond()*1000);
		String generateRangeString = Event.generateRangeString(dto);

		assertEquals("[2021-05-12 04:20:00.000000,2021-05-12 06:20:00.000001)", generateRangeString);
	}
}
