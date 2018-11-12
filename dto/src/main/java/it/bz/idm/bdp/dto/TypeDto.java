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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import io.swagger.annotations.ApiModelProperty;

public class TypeDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1224947780318447560L;
	@ApiModelProperty (notes = "The unique ID of the type.")
	private String id;
	@ApiModelProperty (notes = "The unit of measurement of the type.")
	private String unit;
	private Map<String,String> desc = new HashMap<String, String>();
	private String typeOfMeasurement;
	private Set<Integer> aquisitionIntervalls = new TreeSet<Integer>();
	
	public TypeDto() {
	}
	public TypeDto(String id, Integer interval) {
		this.id = id;
		if (interval!= null)
			this.aquisitionIntervalls.add(interval);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Map<String, String> getDesc() {
		return desc;
	}
	public void setDesc(Map<String, String> desc) {
		this.desc = desc;
	}
	public String getTypeOfMeasurement() {
		return typeOfMeasurement;
	}
	public void setTypeOfMeasurement(String typeOfMeasurement) {
		this.typeOfMeasurement = typeOfMeasurement;
	}
	public Set<Integer> getAquisitionIntervalls() {
		return aquisitionIntervalls;
	}
	public void setAquisitionIntervalls(Set<Integer> aquisitionIntervalls) {
		this.aquisitionIntervalls = aquisitionIntervalls;
	}
	
}
