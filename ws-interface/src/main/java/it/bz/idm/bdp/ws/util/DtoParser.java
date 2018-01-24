package it.bz.idm.bdp.ws.util;

import java.util.ArrayList;
import java.util.List;

import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.ws.SlimRecordDto;

public class DtoParser {
	public static List<SlimRecordDto> reduce(List<RecordDto> records) {
		List<SlimRecordDto> list = new ArrayList<>();
		for (RecordDto dto: records) {
			if (dto instanceof SimpleRecordDto) {
				SimpleRecordDto sDto = (SimpleRecordDto) dto;
				list.add(new SlimRecordDto(sDto.getTimestamp(), sDto.getValue(), sDto.getPeriod(),null));
			}
				
		}
		return list;
	}
}
