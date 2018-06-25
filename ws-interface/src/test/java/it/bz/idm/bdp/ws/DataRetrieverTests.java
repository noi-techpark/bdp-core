/**
 * ws-interface - Web Service Interface for the Big Data Platform
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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.bz.idm.bdp.ws;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.ws.DataRetriever;
import it.bz.idm.bdp.ws.RestClient;


public class DataRetrieverTests {

	private DataRetriever retriever;
	@Before
	public void doBefore(){
		 retriever= new RestClient(){
			@Override
			public String initIntegreenTypology() {
				return "TestStation";
			}
		};
	}
	
	@Test
	public void testInitDataRetriever(){
		assertNotNull(retriever.integreenTypology);
		assertNotNull(retriever.config);
	}
}
