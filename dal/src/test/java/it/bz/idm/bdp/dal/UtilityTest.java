package it.bz.idm.bdp.dal;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import it.bz.idm.bdp.dto.EventDto;

public class UtilityTest {

	@Test
	public void testRangeCasting() {
		EventDto dto = new EventDto();
		LocalDateTime startTime = LocalDateTime.of(2021, 5, 12, 4, 20);
		LocalDateTime endTime = LocalDateTime.of(2021, 5, 12, 6, 20);
		dto.setEventStart(startTime.atZone(ZoneId.systemDefault()).toEpochSecond()*1000);
		dto.setEventEnd(endTime.atZone(ZoneId.systemDefault()).toEpochSecond()*1000);
		String generateRangeString = Event.generateRangeString(dto);
		
		assertEquals("[2021-05-12 04:20:00.000000,2021-05-12 06:20:00.000000]", generateRangeString);
	}
}
