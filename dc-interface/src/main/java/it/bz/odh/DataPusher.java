/**
 * dc-interface - Data Collector Interface for the Big Data Platform
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
package it.bz.odh;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import it.bz.odh.dto.ProvenanceDto;

/**
 * Basic configuration for any sender providing data to the writer
 *
 * @author Patrick Bertolla
 */
@Component
public abstract class DataPusher implements ODHPushable  {
	protected String integreenTypology;
	protected ProvenanceDto provenance;

	public abstract String initIntegreenTypology();
	public abstract ProvenanceDto defineProvenance();

	/**
	 * Instantiate a new data pusher with a typology defined in implementation
	 */
	@PostConstruct
	public void init() {
		this.integreenTypology = initIntegreenTypology();
		if (this.integreenTypology == null)
			throw new IllegalStateException("You need to provide a valid data source type to continue");
		ProvenanceDto provenance = defineProvenance();
		if (provenance == null || !provenance.isValid())
			throw new IllegalStateException("You need to provide a valid provenance to be able to send data");
		this.provenance = provenance;
	}

	public String getIntegreenTypology() {
		return integreenTypology;
	}
	public void setIntegreenTypology(String integreenTypology) {
		this.integreenTypology = integreenTypology;
	}
}
