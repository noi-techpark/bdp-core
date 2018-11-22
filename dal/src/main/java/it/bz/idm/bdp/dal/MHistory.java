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
package it.bz.idm.bdp.dal;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;

@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class MHistory {

	private Date created_on;
	private Date timestamp;
	@ManyToOne(cascade=CascadeType.ALL)
	private Station station;

	@ManyToOne(cascade=CascadeType.PERSIST)
	private DataType type;

	private Integer period;

	public MHistory() {
		this.created_on = new Date();
	}
	public MHistory(Station station, DataType type, Date timestamp, Integer period) {
		this.station = station;
		this.type = type;
		this.timestamp = timestamp;
		this.period = period;
		this.created_on = new Date();
	}

	public Date getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}

	public static void pushRecords(EntityManager em, String stationType, Object... objects) {
		Object object = objects[0];
		BDPRole adminRole = BDPRole.fetchAdminRole(em);
		if (object instanceof DataMapDto) {
			@SuppressWarnings("unchecked")
			DataMapDto<RecordDtoImpl> dto = (DataMapDto<RecordDtoImpl>) object;
			try{
				for (Entry<String, DataMapDto<RecordDtoImpl>> entry:dto.getBranch().entrySet()){
					Station station = Station.findStation(em, stationType, entry.getKey());
					if (station == null) {
						System.out.println("pushRecords: Station '" + stationType + "/" + entry.getKey() + "' not found. Skipping...");
						continue;
					}
					for(Entry<String,DataMapDto<RecordDtoImpl>> typeEntry : entry.getValue().getBranch().entrySet()) {
						try {
							DataType type = DataType.findByCname(em, typeEntry.getKey());
							if (type == null) {
								System.out.println("pushRecords: Type '" + typeEntry.getKey() + "' not found. Skipping...");
								continue;
							}

							List<? extends RecordDtoImpl> dataRecords = typeEntry.getValue().getData();
							if (dataRecords.isEmpty()) {
								System.out.println("pushRecords: Empty data set. Skipping...");
								continue;
							}

							em.getTransaction().begin();
							M lastEntry = new Measurement().findLatestEntry(em, station, type, null, adminRole);
							Date created_on = new Date();
							Collections.sort(dataRecords);
							long lastEntryTime = (lastEntry != null)?lastEntry.getTimestamp().getTime():0;
							for (RecordDto recordDto : dataRecords){
								if (recordDto instanceof SimpleRecordDto){
									SimpleRecordDto simpleRecordDto = (SimpleRecordDto)recordDto;
									Long dateOfMeasurement = simpleRecordDto.getTimestamp();
									Double value = (Double) simpleRecordDto.getValue();
									if(lastEntryTime < dateOfMeasurement){
										MeasurementHistory record = new MeasurementHistory(station,type,value,new Date(dateOfMeasurement),simpleRecordDto.getPeriod(),created_on);
										em.persist(record);
									}
								}
							}
							SimpleRecordDto newestDto = (SimpleRecordDto) dataRecords.get(dataRecords.size()-1);
							if (lastEntry == null){
								Double value = (Double) newestDto.getValue();
								lastEntry = new Measurement(station, type, value, new Date(newestDto.getTimestamp()), newestDto.getPeriod());
								em.persist(lastEntry);
							}
							else if (newestDto != null && newestDto.getTimestamp()>lastEntryTime){
								Double value = (Double) newestDto.getValue();
								lastEntry.setTimestamp(new Date(newestDto.getTimestamp()));
								lastEntry.setValue(value);
								em.merge(lastEntry);
							}
							em.getTransaction().commit();
						}catch(Exception ex){
							ex.printStackTrace();
							if (em.getTransaction().isActive())
								em.getTransaction().rollback();
							continue;
						}
					}

				}
			}catch(Exception ex){
				ex.printStackTrace();
				if (em.getTransaction().isActive())
					em.getTransaction().rollback();
				throw ex;
			}finally{
				em.clear();
				em.close();
			}
		}
	}
}
