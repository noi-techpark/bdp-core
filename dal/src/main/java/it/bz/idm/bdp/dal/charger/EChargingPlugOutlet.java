/**
 * BDP data - Data Access Layer for the Big Data Platform
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
package it.bz.idm.bdp.dal.charger;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.emobility.OutletDtoV2;

@Entity
public class EChargingPlugOutlet {

	@Id
	@GeneratedValue(generator = "echargingplugoutlet_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "echargingplugoutlet_gen", sequenceName = "echargingplugoutlet_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('echargingplugoutlet_seq')")
	private Long id;

	private String code;

	@ManyToOne
	private Station plug;
	private Double maxCurrent;
	private Double minCurrent;
	private Double maxPower;
	private String plugType;
	private Boolean	hasFixedCable;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Station getPlug() {
		return plug;
	}
	public void setPlug(Station plug) {
		this.plug = plug;
	}
	public Double getMaxCurrent() {
		return maxCurrent;
	}
	public void setMaxCurrent(Double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}
	public Double getMinCurrent() {
		return minCurrent;
	}
	public void setMinCurrent(Double minCurrent) {
		this.minCurrent = minCurrent;
	}
	public Double getMaxPower() {
		return maxPower;
	}
	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}
	public String getPlugType() {
		return plugType;
	}
	public void setPlugType(String plugType) {
		this.plugType = plugType;
	}
	public Boolean getHasFixedCable() {
		return hasFixedCable;
	}
	public void setHasFixedCable(Boolean hasFixedCable) {
		this.hasFixedCable = hasFixedCable;
	}
	public static List<OutletDtoV2> toDto(List<EChargingPlugOutlet> outlets) {
		List<OutletDtoV2> dtos = new ArrayList<OutletDtoV2>();
		for (EChargingPlugOutlet outlet : outlets){
			OutletDtoV2 dto = toDto(outlet);
			dtos.add(dto);
		}
		return dtos;
	}
	private static OutletDtoV2 toDto(EChargingPlugOutlet outlet) {
		OutletDtoV2 dto = new OutletDtoV2();
		dto.setHasFixedCable(outlet.getHasFixedCable());
		dto.setMaxCurrent(outlet.getMaxCurrent());
		dto.setMinCurrent(outlet.getMinCurrent());
		dto.setMaxPower(outlet.getMaxPower());
		dto.setOutletTypeCode(outlet.getPlugType());
		dto.setId(outlet.getCode());
		return dto;
	}

}
