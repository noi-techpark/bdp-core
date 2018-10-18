/**
 * dto - Data Transport Objects for an object-relational mapping
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
public class CarsharingVehicleDto extends StationDto
{
	private static final long serialVersionUID = -6739458663647210882L;
	public static final String IDENTIFIER = "id";
   public static final String STATE = "state";
   public static final String CREATED_ON = "created_on";
   public static final String TIMESTAMP = "timestamp";

   String licensePlate;
   String model;
   String brand;
   String showType;
   String stationId;

   public void setVehicleUID(String vehicleUID)
   {
      this.setId(vehicleUID);
   }

   public String getLicensePlate()
   {
      return this.licensePlate;
   }

   public void setLicensePlate(String licensePlate)
   {
      this.licensePlate = licensePlate;
   }

   public String getModel()
   {
      return this.model;
   }

   public void setModel(String model)
   {
      this.model = model;
   }

   public String getBrand()
   {
      return this.brand;
   }

   public void setBrand(String brand)
   {
      this.brand = brand;
   }

   public String getShowType()
   {
      return this.showType;
   }

   public void setShowType(String showType)
   {
      this.showType = showType;
   }

   public void setStation(String parentStationId)
   {
      this.stationId = parentStationId;
   }

   public String getStationId() {
	   return stationId;
   }

   public void setStationId(String stationId) {
	   this.stationId = stationId;
   }


}
