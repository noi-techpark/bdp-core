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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_EMPTY)
public class SimpleRecordDto extends RecordDtoImpl {

	/**
	 *
	 */
	private static final long serialVersionUID = 5703758724961079739L;
	private Object value;
	private Integer period;


	public SimpleRecordDto() {
	}
	public SimpleRecordDto(Long timestamp,Double value) {
		super();
		this.timestamp = timestamp;
		this.value = value;
	}
	public SimpleRecordDto(Long timestamp, Object value, Integer period) {
		super();
		this.timestamp = timestamp;
		this.value = value;
		this.period = period;
	}
	@Override
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	@JsonIgnore
	public boolean isValid() {
		return value!=null&&timestamp!=null&&period!=null;
	}

	@Override
	public String toString() {
		return "SimpleRecordDto [timestamp=" + timestamp + ", value=" + value + ", period=" + period + "]";
	}

}

