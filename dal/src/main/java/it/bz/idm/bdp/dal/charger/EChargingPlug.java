/**
 * BDP data - Data Access Layer for the Big Data Platform
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
package it.bz.idm.bdp.dal.charger;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.emobility.EchargingPlugDto;
import it.bz.idm.bdp.dto.emobility.OutletDtoV2;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;

import com.vividsolutions.jts.geom.Point;

@Entity
public class EChargingPlug extends MeasurementStation {

	@OneToMany(mappedBy="plug",fetch=FetchType.EAGER,cascade = CascadeType.ALL)
	private List<EChargingPlugOutlet> outlets = new ArrayList<EChargingPlugOutlet>();

	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station station:resultList){
			EChargingPlugBasicData basic = (EChargingPlugBasicData) new EChargingPlugBasicData().findByStation(em, station);
			EChargingPlug plug = (EChargingPlug) station;
			Double x = null,y = null;
			if (plug.getPointprojection() != null){
				y = plug.getPointprojection().getY();
				x = plug.getPointprojection().getX();
			}
			List<OutletDtoV2> dtos = EChargingPlugOutlet.toDto(plug.getOutlets());
			if (basic != null && basic.geteStation()!= null){
				EchargingPlugDto dto = new EchargingPlugDto(plug.getStationcode(),plug.getName(),y,x,basic.geteStation().getStationcode(),dtos);
				dto.setMunicipality(station.getMunicipality());
				stationList.add(dto);
			}
		}
		return stationList;
	}
	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
		if (dto instanceof EchargingPlugDto){
			EchargingPlugDto plugDto = (EchargingPlugDto) dto;
			EChargingStation eStation = (EChargingStation) new EChargingStation().findStation(em,plugDto.getParentStation());
			EChargingPlug plug = (EChargingPlug) station;
			EChargingPlugBasicData plugBasic = (EChargingPlugBasicData) new EChargingPlugBasicData().findByStation(em,plug);
			if (plugBasic == null){
				plugBasic = new EChargingPlugBasicData();
				plugBasic.setStation(plug);
				em.persist(plugBasic);
			}
			plugBasic.seteStation(eStation);
			for (OutletDtoV2 outletDto:plugDto.getOutlets()){
				EChargingPlugOutlet outlet = null;
				for (EChargingPlugOutlet c : plug.getOutlets())
					if (c.getCode().equals(outletDto.getId()))
						outlet = c;
				if (outlet == null){
					outlet = new EChargingPlugOutlet();
					outlet.setCode(outletDto.getId());
					outlet.setPlug(plug);
					plug.getOutlets().add(outlet);
				}
				outlet.setMaxCurrent(outletDto.getMaxCurrent());
				outlet.setMinCurrent(outletDto.getMinCurrent());
				outlet.setMaxPower(outletDto.getMaxPower());
				outlet.setPlugType(outletDto.getOutletTypeCode());
				outlet.setHasFixedCable(outletDto.getHasFixedCable());

			}
			Point pos = plug.getPointprojection();
			if (pos != null)
				plug.setPointprojection(pos);
			plug.setName(plugDto.getName());
			em.merge(plugBasic);
			em.merge(plug);
		}
	}
	public List<EChargingPlugOutlet> getOutlets() {
		return outlets;
	}

	public void setOutlets(List<EChargingPlugOutlet> outlets) {
		this.outlets = outlets;
	}
	public List<Station> findByParent(EntityManager em, Station station) {
		EChargingStation echargingStation = (EChargingStation) station;
		TypedQuery<Station> query = em.createQuery("Select plug from EChargingPlugBasicData b join b.station plug join b.eStation s where s = :station", Station.class);
		query.setParameter("station", echargingStation);
		return query.getResultList();
	}
}
