/**
 * BDP data - Data Access Layer for the Big Data Platform
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
package it.bz.idm.bdp.dal.util;

import it.bz.idm.bdp.dto.ExceptionDto;

public class JPAException extends RuntimeException {

	private static final long serialVersionUID = -8271639898842999188L;

	private ExceptionDto exceptionDto = new ExceptionDto();

	/*
	 * Used to determine correct DTO schemas
	 */
	private Class<?> schemaDtoClass;

	public JPAException(String error, int httpCode, Throwable cause) {
		super(error, cause);
		exceptionDto.setStatus(new Integer(httpCode));
		exceptionDto.setDescription(error);
	}

	public JPAException(String error, Throwable cause) {
		super(error, cause);
		exceptionDto.setDescription(error);
	}

	public JPAException(String error) {
		super(error);
		exceptionDto.setDescription(error);
	}

	public JPAException(String error, int httpCode) {
		super(error);
		exceptionDto.setStatus(new Integer(httpCode));
		exceptionDto.setDescription(error);
	}

	public JPAException(String error, Class<?> schemaDtoClass) {
		this(error);
		this.schemaDtoClass = schemaDtoClass;
	}

	public JPAException(String error, int httpCode, Class<?> schemaDtoClass) {
		this(error, httpCode);
		this.schemaDtoClass = schemaDtoClass;
	}

	public JPAException(String error, int httpCode, Throwable cause, Class<?> schemaDtoClass) {
		this(error, httpCode, cause);
		this.schemaDtoClass = schemaDtoClass;
	}

	public ExceptionDto getExceptionDto() {
		return exceptionDto;
	}

	public void setExceptionDto(ExceptionDto dto) {
		this.exceptionDto = dto;
	}

	public Class<?> getDtoClass() {
		return schemaDtoClass;
	}

	public void setDtoClass(Class<?> dtoClass) {
		this.schemaDtoClass = dtoClass;
	}
}
