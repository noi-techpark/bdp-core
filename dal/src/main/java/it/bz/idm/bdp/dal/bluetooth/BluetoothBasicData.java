/**
 * BDP data - Data Access Layer for the Big Data Platform
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
package it.bz.idm.bdp.dal.bluetooth;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;

@Entity
@Table(name="bluetoothbasicdata",schema="intime")
public class BluetoothBasicData extends BasicData {
	
	private String sim_number;
	private String sim_serial;
	
	public String getSim_number() {
		return sim_number;
	}
	public void setSim_number(String sim_number) {
		this.sim_number = sim_number;
	}
	public String getSim_serial() {
		return sim_serial;
	}
	public void setSim_serial(String sim_serial) {
		this.sim_serial = sim_serial;
	}
	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		// TODO Auto-generated method stub
		return null;
	}

}
