// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto;

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
