/**
 * writer - Data Writer for the Big Data Platform
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
package it.bz.idm.bdp.writer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import it.bz.idm.bdp.dal.util.JPAUtil;

/**
 * Job scheduler which should handle maintenance jobs on the big data platform
 *
 * @author Patrick Bertolla
 */
@Configuration
@EnableScheduling
public class JobScheduler {

	@Value("classpath:META-INF/sql/opendatarules.sql")
	private Resource sql;

	/**
	 * Updates permissions by executing the given script
	 * @throws Exception
	 */
	@Scheduled(cron="0 0 * * * *")
	public void updateOpenData() throws Exception {
		JPAUtil.executeNativeQueries(sql.getInputStream());
	}

}
