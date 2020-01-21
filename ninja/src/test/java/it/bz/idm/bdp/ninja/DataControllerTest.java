package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;

import org.junit.Test;

import it.bz.idm.bdp.ninja.controller.DataController;

public class DataControllerTest extends DataController {

	@Test
	public void testDate() {
		ZonedDateTime dt = getDateTime("2019-11-02");
		assertEquals(2019, dt.getYear());
		assertEquals(11, dt.getMonthValue());
		assertEquals(2, dt.getDayOfMonth());
		assertEquals(0, dt.getHour());
		assertEquals(0, dt.getMinute());
		assertEquals(0, dt.getSecond());
	}

	@Test
	public void testDateHour() {
		ZonedDateTime dt = getDateTime("2019-11-02T15");
		assertEquals(2019, dt.getYear());
		assertEquals(11, dt.getMonthValue());
		assertEquals(2, dt.getDayOfMonth());
		assertEquals(15, dt.getHour());
		assertEquals(0, dt.getMinute());
		assertEquals(0, dt.getSecond());
		assertEquals(0, dt.getNano());
	}

	@Test
	public void testDateTimeHourMinuteSecond() {
		ZonedDateTime dt = getDateTime("2019-11-02T15:30:03");
		assertEquals(2019, dt.getYear());
		assertEquals(11, dt.getMonthValue());
		assertEquals(2, dt.getDayOfMonth());
		assertEquals(15, dt.getHour());
		assertEquals(30, dt.getMinute());
		assertEquals(3, dt.getSecond());
		assertEquals(0, dt.getNano());
	}

	@Test
	public void testDateTimeHourMinuteSecondNano() {
		ZonedDateTime dt = getDateTime("2019-11-02T15:30:03.127");
		assertEquals(2019, dt.getYear());
		assertEquals(11, dt.getMonthValue());
		assertEquals(2, dt.getDayOfMonth());
		assertEquals(15, dt.getHour());
		assertEquals(30, dt.getMinute());
		assertEquals(3, dt.getSecond());
		assertEquals(127000000, dt.getNano());
	}
	@Test
	public void testDateTimeWithZone() {
		ZonedDateTime dt = getDateTime("2019-11-02T15:30:33.123+0200");
		assertEquals(2019, dt.getYear());
		assertEquals(11, dt.getMonthValue());
		assertEquals(2, dt.getDayOfMonth());
		assertEquals(15, dt.getHour());
		assertEquals(30, dt.getMinute());
		assertEquals(33, dt.getSecond());
		assertEquals(123000000, dt.getNano());
	}
	@Test
	public void testDateTimeZone2() {
		ZonedDateTime dt = getDateTime("2019-11-02T15:30:33.123Z");
		assertEquals(2019, dt.getYear());
		assertEquals(11, dt.getMonthValue());
		assertEquals(2, dt.getDayOfMonth());
		assertEquals(15, dt.getHour());
		assertEquals(30, dt.getMinute());
		assertEquals(33, dt.getSecond());
		assertEquals(123000000, dt.getNano());
	}
}
