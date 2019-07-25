/**
 * BDP data - Data Access Layer for the Big Data Platform
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
package it.bz.idm.bdp.ninja.utils.simpleexception;

import java.util.HashMap;
import java.util.Map;

/**
 * JPAException, which is a well-described runtime exception, ready for API consumers.
 * The main goal for such an exception is to provide enough information to an API consumer
 * to handle API errors as easy as possible. Do not expose internal errors.
 *
 * @author Peter Moser
 *
 */
public class SimpleException extends RuntimeException {

	private static final long serialVersionUID = -8271639898842999188L;

	private String description;
	private ErrorCodeInterface id;
	private Map<String, Object> data;

	public SimpleException(ErrorCodeInterface id, Object... params) {
		super(String.format(id.getMsg(), params));
		this.id = id;
	}

	public SimpleException(ErrorCodeInterface id) {
		this(id, (Object[])null);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ErrorCodeInterface getId() {
		return id;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void addData(String key, Object data) {
		if (this.data == null) {
			this.data = new HashMap<String, Object>();
		}
		this.data.put(key, data);
	}

	public void setData(Map<String, Object> dataMap) {
		this.data = dataMap;
	}

}
