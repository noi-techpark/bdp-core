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

public class JPAException extends RuntimeException {

	private static final long serialVersionUID = -8271639898842999188L;

	private String error;
	private String hint;

	public JPAException(String error, String hint, Throwable cause) {
		super(error, cause);
		this.setError(error);
		this.setHint(hint);
	}

	public JPAException(String error, String hint) {
		super(error);
		this.setError(error);
		this.setHint(hint);
	}

	public JPAException(String error, Throwable cause) {
		super(error, cause);
		this.setError(error);
		this.setHint(null);
	}

	public JPAException(String error) {
		super(error);
		this.setError(error);
		this.setHint(null);
	}

	public JPAException(Throwable cause) {
		super("UNKNOWN", cause);
		this.setError(null);
		this.setHint(null);
	}

	public JPAException() {
		super("UNKNOWN");
		this.setError(null);
		this.setHint(null);
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error == null ? "UNKNOWN" : error;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint == null ? "" : hint;
	}
}
