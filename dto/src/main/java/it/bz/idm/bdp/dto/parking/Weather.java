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

public class Weather implements ObservationMetaInfo{
	private String description = "no data available";
	private int id= -1;
	private int mintemp=-1;
	private int maxtemp = -1;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMintemp() {
		return mintemp;
	}
	public void setMintemp(int mintemp) {
		this.mintemp = mintemp;
	}
	public int getMaxtemp() {
		return maxtemp;
	}
	public void setMaxtemp(int maxtemp) {
		this.maxtemp = maxtemp;
	}
	
	

}
