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
package it.bz.idm.bdp;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.ProvenanceDto;

/**
 * Basic configuration for any sender providing data to the writer
 *
 * @author Patrick Bertolla
 */
@Component
public abstract class DataPusher implements IntegreenPushable  {

	public static final int STATION_CHUNK_SIZE = 25;

	private static final String APPLICATION_PROPERTIES_FILE 		= "application.properties";
	protected static final String ENDPOINT_KEY		   				= "bdp_endpoint";
	protected static final String HOST_KEY							= "bdp_host";
	protected static final String PORT_KEY							= "bdp_port";

	protected static String DEFAULT_HOST 							= "localhost";
	protected static Integer DEFAULT_PORT							= 8080;
	protected static String DEFAULT_ENDPOINT						= "";

	protected Configuration config;
	protected String integreenTypology;
	protected ProvenanceDto provenance;

	public abstract void connectToDataCenterCollector();
	public abstract String initIntegreenTypology();
	public abstract ProvenanceDto defineProvenance();

	/**
	 * Instantiate a new data pusher with a typology defined in implementation
	 */
	@PostConstruct
	public void init() {
		initConfig();
		connectToDataCenterCollector();
		this.integreenTypology = initIntegreenTypology();
		if (this.integreenTypology == null)
			throw new IllegalStateException("You need to provide a valid data source type to continue");
		ProvenanceDto provenanceCandidate = defineProvenance();
		if (!ProvenanceDto.isValid(provenanceCandidate))
			throw new IllegalStateException("You need to provide a valid provenance to be able to send data");
		this.provenance = provenanceCandidate;
	}

	/**
	 * set host, port and endpoint of the writer module
	 */
	protected void initConfig() {
		if (config == null)
			try {
				config = new PropertiesConfiguration(APPLICATION_PROPERTIES_FILE);
				DEFAULT_HOST =  config.getString(HOST_KEY);
				DEFAULT_PORT =	config.getInt(PORT_KEY);
				DEFAULT_ENDPOINT = config.getString(ENDPOINT_KEY);
			} catch (ConfigurationException e1) {
				e1.printStackTrace();
			}
	}

	public String getIntegreenTypology() {
		return integreenTypology;
	}
	public void setIntegreenTypology(String integreenTypology) {
		this.integreenTypology = integreenTypology;
	}

}
