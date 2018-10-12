/**
 * dto - Data Transport Objects for the Big Data Platform
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

public class FullRecordDto extends RecordDtoImpl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5279857732736895894L;
	private String station;
	private String type;
	private Object value;
	private Integer period;

	
	public FullRecordDto(Long timestamp, Object value, String station, String type, Integer period) {
		this.timestamp = timestamp;
		this.value = value;
		this.station = station;
		this.type = type;
		this.period = period;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Object getValue() {
		return this.value;
	}
	
	
	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	@Override
	public boolean validate() {
		return this.getStation()!=null && this.getType()!=null && super.validate();
	}

}
