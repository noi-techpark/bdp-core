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

public abstract class RecordDtoImpl implements RecordDto,Comparable<RecordDtoImpl>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1124149647267291299L;
	protected Long timestamp;

	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public abstract Object getValue();

	public boolean validate() {
		return this.timestamp != null && this.getValue() != null;
	}
	
	@Override
	public int compareTo(RecordDtoImpl o) {
		return this.timestamp > o.timestamp ? 1:-1;
	}

	
}
