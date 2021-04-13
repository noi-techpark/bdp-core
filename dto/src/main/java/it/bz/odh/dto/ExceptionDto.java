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
package it.bz.odh.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * A representation of an Exception shown to the consumers of an API. We want to
 * provide an error, an error code and a description that should help to solve that error.
 * In addition, if the error comes from a wrongly provided JSON, the valid JSON schema
 * gets shown. Finally, if the error triggers a HTTP error code, that status gets reported
 * as well.
 *
 * @author Peter Moser
 */
public class ExceptionDto implements Serializable {
	private static final long serialVersionUID = -2644337056228195945L;
	private Integer status;
	private String name;
	private String description;
	private Map<String, Object> correctSchema;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, Object> getJsonSchema() {
		return correctSchema;
	}
	public void setJsonSchema(Map<String, Object> jsonSchema) {
		this.correctSchema = jsonSchema;
	}
}
