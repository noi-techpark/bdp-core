/**
 * Big data platform - Data Writer for the Big Data Platform, that writes changes to the database
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
package it.bz.idm.bdp.writer;
import java.io.IOException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.metadata.XmlRpcSystemImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class MyXmlRpcServlet extends XmlRpcServlet{

	private static final long serialVersionUID = 1L;

	@Override
	protected XmlRpcHandlerMapping newXmlRpcHandlerMapping()
			throws XmlRpcException {
		URL url = MyXmlRpcServlet.class.getClassLoader().getResource("XmlRpcServlet.properties");
		if (url == null) {
			throw new XmlRpcException("Failed to locate resource XmlRpcServlet.properties");
		}
		try {
			PropertyHandlerMapping newPropertyHandlerMapping = newPropertyHandlerMapping(url);
			XmlRpcSystemImpl.addSystemHandler(newPropertyHandlerMapping);
			return newPropertyHandlerMapping;
		} catch (IOException e) {
			throw new XmlRpcException("Failed to load resource " + url + ": " + e.getMessage(), e);
		}
	}
}
