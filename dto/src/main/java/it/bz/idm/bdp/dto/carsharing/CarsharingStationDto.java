/**
 * dto - Data Transport Objects for an object-relational mapping
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
/*
carsharing-ds: car sharing datasource for the integreen cloud

Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.bz.idm.bdp.dto.carsharing;

import it.bz.idm.bdp.dto.StationDto;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class CarsharingStationDto extends StationDto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7245403194705414759L;
	public static final String IDENTIFIER ="id";
	public static final String VALUE_IDENTIFIER = "free";
	public static final String TIMESTAMP = "timestamp";
	public static final String CREATED_ON = "created_on";
	boolean  hasFixedParking;
	BookMode bookMode;
	StationAccess access;
	Company company;


	public CarsharingStationDto() {
		this.setOrigin("CARSHARINGBZ");
	}
	public void setUid(String uid)
	{
		this.setId(uid);
	}

	public void setHasFixedParking(boolean hasFixedParking)
	{
		this.hasFixedParking = hasFixedParking;
	}

	public boolean isHasFixedParking()
	{
		return this.hasFixedParking;
	}

	public void setBookMode(BookMode bookMode)
	{
		this.bookMode = bookMode;
	}

	public BookMode getBookMode()
	{
		return this.bookMode;
	}

	public StationAccess getAccess() {
		return access;
	}

	public void setAccess(StationAccess access) {
		this.access = access;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	

}
