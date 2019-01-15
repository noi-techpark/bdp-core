/**
 * ws-interface - Web Service Interface for the Big Data Platform
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
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
			SlimRecordDto record = reduce(dto);
			list.add(record);
		}
		return list;
	}

	public static SlimRecordDto reduce(RecordDto dto) {
		SlimRecordDto slimDto = null;
		if (dto instanceof SimpleRecordDto) {
			SimpleRecordDto sDto = (SimpleRecordDto) dto;
			slimDto = new SlimRecordDto(sDto.getTimestamp(), sDto.getValue(), sDto.getPeriod(),null);
		}
		return slimDto;
	}
}
