/**
 * writer - Data Writer for the Big Data Platform
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

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dto.ExceptionDto;

@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(final Exception ex) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
	}
	@ExceptionHandler(JPAException.class)
	public ResponseEntity<Object> handleJPA(JPAException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex);
	}
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(status,ex);
	}
	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST,ex);
	}
	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex);
	}
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex);
	}
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(HttpStatus.NOT_ACCEPTABLE,ex);
	}
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,ex);
	}
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST,ex);
	}
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex);
	}
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(HttpStatus.BAD_REQUEST,ex);
	}
	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(status,ex);
	}
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(status,ex);
	}
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(status,ex);
	}
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(status,ex);
	}
	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(status,ex);
	}
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponse(status,ex);
	}
	private ResponseEntity<Object> buildResponse(HttpStatus httpStatus, Exception ex) {
		ExceptionDto dto;
		if (ex instanceof JPAException) {
			dto = ((JPAException) ex).getDto();
			if (ex.getCause() != null) {
				dto.setDescription(ex.getCause().getMessage());
			}
		} else {
			dto = new ExceptionDto();
		}
		if (dto.getDescription() == null)
			dto.setDescription(ex.getMessage());
		if (dto.getStatus() == null) {
			dto.setStatus(httpStatus.value());
			dto.setName(httpStatus.getReasonPhrase());
		} else if (dto.getName() == null) {
			dto.setName(HttpStatus.valueOf(dto.getStatus()).getReasonPhrase());
		}
		httpStatus = HttpStatus.valueOf(dto.getStatus());
		return ResponseEntity.status(httpStatus).body(dto);
	}
}
