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
package it.bz.idm.bdp.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * <p>
 * Container for all measurements collected from a specific data collector
 * associated to a specific stationtype.<br/>
 * It's a tree structure and it's possible to create as many layers as
 * required.<br/>
 * Currently we only support this kind of layering:<br/>
 * Station --> DataType --> Measurements
 * </p>
 *
 * @author Patrick Bertolla
 * @author Peter Moser
 *
 */
public class DataMapDto <X extends RecordDtoImpl> implements Serializable{

	private static final long serialVersionUID = -6053193762265167013L;
	private String name = "(default)";
	private List<RecordDtoImpl> data = new ArrayList<RecordDtoImpl>();
	private Map<String,DataMapDto<RecordDtoImpl>> branch = new TreeMap<String,DataMapDto<RecordDtoImpl>>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataMapDto() {
		super();
	}
	public DataMapDto(List<RecordDtoImpl> data) {
		this.data = data;
	}
	public List<RecordDtoImpl> getData(){
		return data;
	}
	public void setData(List<RecordDtoImpl> records) {
		this.data = records;
	}
	public Map<String, DataMapDto<RecordDtoImpl>> getBranch() {
		return branch;
	}

	/**
	 * Insert a new branch if the key does not exist, otherwise give the existing branch back.
	 *
	 * @param key
	 *            to find the branch inside the data map
	 * @return the existing branch associated to the given key, or a new branch with that key
	 */
	public DataMapDto<RecordDtoImpl> upsertBranch(String key) {
		DataMapDto<RecordDtoImpl> existingMap = branch.get(key);

		if (existingMap == null) {
			existingMap = new DataMapDto<RecordDtoImpl>();
			branch.put(key, existingMap);
		}

		return existingMap;
	}

}
