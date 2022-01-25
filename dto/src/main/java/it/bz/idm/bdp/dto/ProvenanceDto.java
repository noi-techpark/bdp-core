/**
 * dto - Data Transport Objects for an object-relational mapping
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
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

public class ProvenanceDto implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 2627858984135614589L;

	protected String uuid;

	protected String lineage;

	protected String dataCollector;

	protected String dataCollectorVersion;

	public ProvenanceDto() {
	}
	public ProvenanceDto(String uuid, String dataCollector, String dataCollectorVersion, String lineage) {
		this.uuid=uuid;
		this.dataCollector = dataCollector;
		this.dataCollectorVersion = dataCollectorVersion;
		this.lineage = lineage;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLineage() {
		return lineage;
	}

	public void setLineage(String lineage) {
		this.lineage = lineage;
	}

	public String getDataCollector() {
		return dataCollector;
	}

	public void setDataCollector(String dataCollector) {
		this.dataCollector = dataCollector;
	}

	public String getDataCollectorVersion() {
		return dataCollectorVersion;
	}

	public void setDataCollectorVersion(String dataCollectorVersion) {
		this.dataCollectorVersion = dataCollectorVersion;
	}
	public static boolean isValid(ProvenanceDto dto) {
		if (dto == null)
			return false;
		if (dto.lineage == null || dto.lineage.trim().isEmpty())
			return false;
		if (dto.dataCollector == null || dto.dataCollector.trim().isEmpty())
			return false;
		return true;
	}

}
