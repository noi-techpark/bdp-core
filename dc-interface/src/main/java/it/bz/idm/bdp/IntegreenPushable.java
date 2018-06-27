/**
 * dc-interface - Data Collector Interface for the Big Data Platform
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
package it.bz.idm.bdp;

import java.util.List;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

public interface IntegreenPushable {
	
	public abstract <T> DataMapDto<RecordDtoImpl> mapData(T data);
	public abstract Object pushData(String datasourceName, DataMapDto<? extends RecordDtoImpl> dto);
	public abstract Object syncStations(String datasourceName, StationList dtos);
	public abstract Object syncDataTypes(String datasourceName,List<DataTypeDto> data);
	public abstract Object getDateOfLastRecord(String stationCode,String dataType,Integer period);
}
