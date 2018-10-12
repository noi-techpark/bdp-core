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
package it.bz.idm.bdp.dto.emobility;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.bz.idm.bdp.dto.StationDto;


public class ChargerDto extends StationDto{

	/**
	 * 
	 */
	@JsonProperty("code")
	private String id;
	private static final long serialVersionUID = -4034114700896767374L;
	private String assetProvider;
	private String state;

	private String address;
	private String postalCode;
	private String country;
	private String city;
	private String region;
	private Double distance;
	private Integer chargingPointsCount;
	private List<String> supportedPlugs;
	private List<ChargingPointsDto> chargingPoints;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAssetProvider() {
		return assetProvider;
	}
	public void setAssetProvider(String assetProvider) {
		this.assetProvider = assetProvider;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public Integer getChargingPointsCount() {
		return chargingPointsCount;
	}
	public void setChargingPointsCount(Integer chargingPointsCount) {
		this.chargingPointsCount = chargingPointsCount;
	}
	public List<String> getSupportedPlugs() {
		return supportedPlugs;
	}
	public void setSupportedPlugs(List<String> supportedPlugs) {
		this.supportedPlugs = supportedPlugs;
	}
	public List<ChargingPointsDto> getChargingPoints() {
		return chargingPoints;
	}
	public void setChargingPoints(List<ChargingPointsDto> chargingPoints) {
		this.chargingPoints = chargingPoints;
	}
	
}
