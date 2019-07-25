/**
 * reader - Data Reader for the Big Data Platform, that queries the database for web-services
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
package it.bz.idm.bdp.ninja.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.bz.idm.bdp.ninja.utils.simpleexception.SimpleException;

/**
 * API exception handler mapping every exception to a serializable object
 *
 * @author Peter Moser
 */
@ControllerAdvice
public class ErrorResponseConfig extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ErrorResponseConfig.class);

	@ExceptionHandler
	public ResponseEntity<Object> handleException(Exception ex) {
		ex.printStackTrace(System.err);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, ex.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<Object> handleException(SimpleException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex, ex.getMessage());
	}

	@ExceptionHandler
	public ResponseEntity<Object> handleException(DataAccessException ex) {
		Throwable cause = ex.getCause();
		if (cause instanceof PSQLException) {
			return buildResponse(HttpStatus.BAD_REQUEST, (PSQLException) cause, ((PSQLException) cause).getMessage());
		}
		return buildResponse(HttpStatus.BAD_REQUEST, ex, ex.getCause().getMessage());
	}

	private ResponseEntity<Object> buildResponse(final HttpStatus httpStatus, final Exception exception, final String logRef) {
		final String message = Optional.of(exception.getMessage()).orElse(exception.getClass().getSimpleName());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", message);
		map.put("timestamp", new Date());
		map.put("code", httpStatus.value());
		map.put("error", httpStatus.getReasonPhrase());
		if (exception instanceof SimpleException) {
			SimpleException se = (SimpleException) exception;
			if (se.getDescription() != null)
				map.put("description", se.getDescription());
			if (se.getData() != null && !se.getData().isEmpty()) {
				map.put("info", se.getData());
			}
		}

		if (log.isDebugEnabled()) {
			exception.printStackTrace(System.err);
		}
		return new ResponseEntity<Object>(map, httpStatus);
	}
}
