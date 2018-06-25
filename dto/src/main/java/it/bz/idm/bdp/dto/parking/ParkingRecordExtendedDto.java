/**
 * dto - Data Transport Objects for an object-relational mapping
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
package it.bz.idm.bdp.dto.parking;

public class ParkingRecordExtendedDto extends ParkingRecordDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = -285380889738388646L;
	private Long created_on;
	
	public ParkingRecordExtendedDto() {
	}
	
	public ParkingRecordExtendedDto(Long lastupdate, int abs, Long date) {
		super(lastupdate,abs);
		this.created_on = date;
	}

	public Long getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Long created_on) {
		this.created_on = created_on;
	}
	
}
