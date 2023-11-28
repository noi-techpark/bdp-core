// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.writer.dal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.opendatahub.timeseries.bdp.writer.dal.util.PropertiesWithEnv;
import com.opendatahub.timeseries.bdp.dto.dto.EventDto;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

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
		dto.setEventStart(
			startTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
		);
		dto.setEventEnd(
			endTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
		);
		assertEquals(
			"[2021-05-12 04:20:00.000000,2021-05-12 06:20:00.000001)",
			dto.getEventIntervalAsString()
		);
	}

	@Test
	public void testRangeCastingInfinity() {
		EventDto dto = new EventDto();
		LocalDateTime startTime = LocalDateTime.of(2021, 5, 12, 4, 20);
		dto.setEventStart(
			startTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
		);
		dto.setEventEnd(null);
		assertEquals(
			"[2021-05-12 04:20:00.000000,)",
			dto.getEventIntervalAsString()
		);
	}
}
