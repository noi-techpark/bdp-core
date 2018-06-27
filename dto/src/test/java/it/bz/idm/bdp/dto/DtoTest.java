/**
 * dto - Data Transport Objects for an object-relational mapping
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
package it.bz.idm.bdp.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DtoTest {
	
	@Test
	public void testEmptyDataSet() {
		DataMapDto<RecordDtoImpl> dto = new DataMapDto<RecordDtoImpl>();
		assertTrue(dto.getData().isEmpty());
		DataMapDto<RecordDtoImpl> childMapDto = new DataMapDto<>();
		List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
		records.add(new SimpleRecordDto(121233l,1d));
		childMapDto.setData(records);
		dto.getBranch().put("station1", childMapDto);
		DataMapDto<RecordDtoImpl> childOfChildMapDto = new DataMapDto<>();
		List<RecordDtoImpl> records2 = new ArrayList<>();
		records2.add(new SimpleRecordDto(324l,2d));
		childOfChildMapDto.setData(records2);
		childMapDto.getBranch().put("precipitation", childOfChildMapDto);

		assertFalse(childMapDto.getData().isEmpty());
		assertTrue(dto.getData().isEmpty());
		assertEquals(((SimpleRecordDto)childOfChildMapDto.getData().get(0)).getValue(),new Double(2));

		
	}

}
