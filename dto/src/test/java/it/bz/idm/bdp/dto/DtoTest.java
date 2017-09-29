package it.bz.idm.bdp.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DtoTest {
	
	@Test
	public void testEmptyDataSet() {
		DataMapDto dto = new DataMapDto();
		assertTrue(dto.getData().isEmpty());
		DataMapDto childMapDto = new DataMapDto();
		List<SimpleRecordDto> records = new ArrayList<SimpleRecordDto>();
		records.add(new SimpleRecordDto(121233l,1d));
		childMapDto.setData(records);
		dto.getBranch().put("station1", childMapDto);
		DataMapDto childOfChildMapDto = new DataMapDto();
		List<SimpleRecordDto> records2 = new ArrayList<>();
		records2.add(new SimpleRecordDto(324l,2d));
		childOfChildMapDto.setData(records2);
		childMapDto.getBranch().put("precipitation", childOfChildMapDto);

		assertTrue(!childMapDto.getData().isEmpty());
		assertTrue(!dto.getData().isEmpty());
		assertEquals(((SimpleRecordDto)dto.getData().get(0)).getValue(),new Double(1));
		assertEquals(((SimpleRecordDto)childMapDto.getData().get(0)).getValue(),new Double(1));
		assertEquals(((SimpleRecordDto)childOfChildMapDto.getData().get(0)).getValue(),new Double(2));

		
	}

}
