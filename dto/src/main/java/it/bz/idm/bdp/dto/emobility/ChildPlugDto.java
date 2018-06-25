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
package it.bz.idm.bdp.dto.emobility;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.bz.idm.bdp.dto.ChildDto;

@JsonInclude(value=Include.NON_EMPTY)
public class ChildPlugDto extends ChildDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7467402590346607912L;
	private List<OutletDtoV2> outlets = new ArrayList<OutletDtoV2>();
	private boolean available;
	public List<OutletDtoV2> getOutlets() {
		return outlets;
	}
	public void setOutlets(List<OutletDtoV2> outlets) {
		this.outlets = outlets;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
}
