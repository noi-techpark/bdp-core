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
package it.bz.idm.bdp.dto.bikesharing;

import it.bz.idm.bdp.dto.StationDto;



public class BikeSharingBikeDto extends StationDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 902769333357686586L;

	/**
	 * 
	 */
	
	private String parentStation;
	private String type;
	private Integer currentState;
	private Integer inStoreHouse;
	public BikeSharingBikeDto() {
	}

	public BikeSharingBikeDto(String bikecode, String stationCode,String name,Integer state,Integer inStoreHouse,  String type) {
		super(bikecode, name, null, null);
		this.parentStation =stationCode;
		this.type = type;
		this.inStoreHouse = inStoreHouse;
		this.currentState = state;
	}

	public BikeSharingBikeDto(String stationcode, String bikesharingCode,
			String name, String type) {
		super(bikesharingCode,name, null, null);
		this.parentStation =stationcode;
		this.type = type;
	}

	public String getParentStation() {
		return parentStation;
	}

	public void setParentStation(String parentStation) {
		this.parentStation = parentStation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getCurrentState() {
		return currentState;
	}

	public void setCurrentState(Integer currentState) {
		this.currentState = currentState;
	}

	public Integer getInStoreHouse() {
		return inStoreHouse;
	}

	public void setInStoreHouse(Integer inStoreHouse) {
		this.inStoreHouse = inStoreHouse;
	}
	
	
}
