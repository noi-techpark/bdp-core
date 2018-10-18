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

import java.io.Serializable;

/**
 *
 * @author Davide Montesin <d@vide.bz>
 */
public class BookMode implements Serializable
{
	private static final long serialVersionUID = -5558793090642205243L;
	boolean canBookAhead;
   boolean spontaneously;
   boolean hasCompanyPreferredVehicle;

   public boolean isCanBookAhead()
   {
      return this.canBookAhead;
   }

   public void setCanBookAhead(boolean canBookAhead)
   {
      this.canBookAhead = canBookAhead;
   }

   public boolean isSpontaneously()
   {
      return this.spontaneously;
   }

   public void setSpontaneously(boolean spontaneously)
   {
      this.spontaneously = spontaneously;
   }

   public boolean isHasCompanyPreferredVehicle()
   {
      return this.hasCompanyPreferredVehicle;
   }

   public void setHasCompanyPreferredVehicle(boolean hasCompanyPreferredVehicle)
   {
      this.hasCompanyPreferredVehicle = hasCompanyPreferredVehicle;
   }

}
