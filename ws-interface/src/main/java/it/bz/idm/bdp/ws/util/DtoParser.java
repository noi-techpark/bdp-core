/**
 * ws-interface - Web Service Interface for the Big Data Platform
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.bz.idm.bdp.ws.util;

import java.util.ArrayList;
import java.util.List;

import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.bluetooth.BluetoothRecordExtendedDto;
import it.bz.idm.bdp.ws.SlimRecordDto;

public class DtoParser {
	public static List<SlimRecordDto> reduce(List<RecordDto> records) {
		List<SlimRecordDto> list = new ArrayList<>();
		for (RecordDto dto: records) {
			if (dto instanceof SimpleRecordDto) {
				SimpleRecordDto sDto = (SimpleRecordDto) dto;
				list.add(new SlimRecordDto(sDto.getTimestamp(), sDto.getValue(), sDto.getPeriod(),null));
			}else if (dto instanceof BluetoothRecordExtendedDto){
				BluetoothRecordExtendedDto bDto = (BluetoothRecordExtendedDto) dto;
				list.add(new SlimRecordDto(bDto.getTimestamp(), bDto.getValue(), null, bDto.getCreated_on()));

			}
				
		}
		return list;
	}
}
