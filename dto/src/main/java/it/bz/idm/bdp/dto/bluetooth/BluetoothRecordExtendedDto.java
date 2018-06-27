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
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
package it.bz.idm.bdp.dto.bluetooth;

public class BluetoothRecordExtendedDto extends BluetoothRecordDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3113420019159385769L;
	private Long created_on;



	public BluetoothRecordExtendedDto() {
	}

	public BluetoothRecordExtendedDto(Long timestamp, Double value,Long created_on) {
		super(timestamp, value);
		this.created_on=created_on;
	}

	public Long getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Long created_on) {
		this.created_on = created_on;
	}


}
