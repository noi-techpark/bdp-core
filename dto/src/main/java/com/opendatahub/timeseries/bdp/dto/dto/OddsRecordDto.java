// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.dto.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Data transfer object which is currently provided by the bluetooth boxes,
 * containing all informations about the single record
 * @author Patrick Bertolla
 */
public class OddsRecordDto implements Serializable{
	private static final long serialVersionUID = 1953232953360864621L;

		private Long utcInMs;

	    private Integer station_id;
	    
	    private Integer local_id;

	    private String mac;

	    private Date gathered_on;
	    
	    private String stationcode;


		public String getStationcode() {
			return stationcode;
		}

		public void setStationcode(String stationcode) {
			this.stationcode = stationcode;
		}

		public Long getUtcInMs() {
			return utcInMs;
		}

		public void setUtcInMs(Long utcInMs) {
			this.utcInMs = utcInMs;
		}

		public Integer getStation_id() {
			return station_id;
		}

		public void setStation_id(Integer station_id) {
			this.station_id = station_id;
		}

		public Integer getLocal_id() {
			return local_id;
		}

		public void setLocal_id(Integer local_id) {
			this.local_id = local_id;
		}

		public String getMac() {
			return mac;
		}

		public void setMac(String mac) {
			this.mac = mac;
		}

		public Date getGathered_on() {
			return gathered_on;
		}

		public void setGathered_on(Date gathered_on) {
			this.gathered_on = gathered_on;
		}

		/**
		 * removes all data which does not contain the minimal amount of information
		 * required to be usefull
		 * @param records list of records to be checked for validity
		 */
		public static void removeCorruptedData(List<OddsRecordDto> records) {
			for (int i=0;i<records.size();i++) {
				OddsRecordDto record = records.get(i);
				if (record == null || record.getGathered_on() == null || record.getMac() == null){
					records.remove(i);
					i--;
				}
			}
			
		}
}

