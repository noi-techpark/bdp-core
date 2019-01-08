/**
 * ws-interface - Web Service Interface for the Big Data Platform
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
package it.bz.idm.bdp.ws;

import org.springframework.beans.factory.annotation.Value;

public abstract class DataRetriever implements IntegreenRunnable {

	@Value("${host}")
	protected String host;

	@Value("${port}")
	protected Integer port;

	@Value("${endpoint}")
	protected String endpoint;

	@Value("${ssl}")
	protected Boolean hasSSL;

	@Value("${bdp.stationtype}")
	protected String stationType;

	protected String accessToken;

	public abstract void connect();

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
