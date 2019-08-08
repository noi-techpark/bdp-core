package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

import it.bz.idm.bdp.ninja.controller.DataController;

public class DataControllerTest extends DataController {

	@Test
	public void testDate() {
		LocalDateTime dt = getDateTime("2019-11-02");
		assertEquals(2019, dt.getYear());
		assertEquals(11, dt.getMonthValue());
		assertEquals(2, dt.getDayOfMonth());
		assertEquals(0, dt.getHour());
		assertEquals(0, dt.getMinute());
		assertEquals(0, dt.getSecond());
	}

	@Test
	public void testDateHour() {
		LocalDateTime dt = getDateTime("2019-11-02T15");
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
		LocalDateTime dt = getDateTime("2019-11-02T15:30:03");
		assertEquals(2019, dt.getYear());
		assertEquals(11, dt.getMonthValue());
		assertEquals(2, dt.getDayOfMonth());
		assertEquals(15, dt.getHour());
		assertEquals(30, dt.getMinute());
		assertEquals(3, dt.getSecond());
		assertEquals(0, dt.getNano());
	}

	@Test
	public void testDateTimeFull() {
		LocalDateTime dt = getDateTime("2019-11-02T15:30:33.123");
		assertEquals(2019, dt.getYear());
		assertEquals(11, dt.getMonthValue());
		assertEquals(2, dt.getDayOfMonth());
		assertEquals(15, dt.getHour());
		assertEquals(30, dt.getMinute());
		assertEquals(33, dt.getSecond());
		assertEquals(123000000, dt.getNano());
	}
}
