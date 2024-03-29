// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.dto.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * <p>
 * Container for all measurements collected from a specific data collector
 * associated to a specific station type.
 * It's a tree structure and it's possible to create as many layers as
 * required.
 * Currently we only support this kind of layering:<br/>
 * <code>Station --> DataType --> Measurements</code>
 * </p>
 *
 * @author Patrick Bertolla
 * @author Peter Moser
 *
 */
public class DataMapDto <X extends RecordDtoImpl> implements Serializable{

	private static final long serialVersionUID = -6053193762265167013L;
	private String name = "(default)";
	private List<RecordDtoImpl> data = new ArrayList<>();
	private Map<String,DataMapDto<RecordDtoImpl>> branch = new TreeMap<>();
	private String provenance;

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
			existingMap = new DataMapDto<>();
			branch.put(key, existingMap);
		}

		return existingMap;
	}

	/**
	 * @param stationId station identifier
	 * @param typeId type identifier
	 * @param dto record to add in the correct leaf of the tree
	 */
	public void addRecord(String stationId, String typeId, SimpleRecordDto dto) {
		if (dto != null && dto.isValid()){
			DataMapDto<RecordDtoImpl> dataMapDto = contstructTree(stationId, typeId);
			dataMapDto.getData().add(dto);
		}
	}

	/**
	 * @param stationId station identifier
	 * @param typeId type identifier
	 * @param dtos records to add in the correct leaf of the tree
	 */
	public void addRecords(String stationId, String typeId, List<SimpleRecordDto> dtos) {
		if (dtos != null && !dtos.isEmpty()){
			DataMapDto<RecordDtoImpl> dataMapDto = contstructTree(stationId, typeId);
			dataMapDto.getData().addAll(dtos);
		}
	}

	/**
	 * @param stationId station identifier
	 * @param typeId type identifier
	 * @param dto records to add in the correct leaf of the tree
	 * @return the data records container identified by station and type
	 * @throws IllegalAccessError this method works only for the root of a tree and not for the station mapping or type mapping objects
	 *
	 */
	private DataMapDto<RecordDtoImpl> contstructTree(String stationId, String typeId){
		if (!this.getData().isEmpty())
			throw new IllegalAccessError("This method only works for the root of your tree");
		if (stationId== null || typeId == null)
			throw new IllegalArgumentException("parameters can not be null");

		DataMapDto<RecordDtoImpl> typeMap = this.getBranch().get(stationId);
		if (typeMap == null) {
			typeMap = new DataMapDto<>();
			this.getBranch().put(stationId, typeMap);
		}
		DataMapDto<RecordDtoImpl> dataMapDto = typeMap.getBranch().get(typeId);
		if (dataMapDto == null) {
			dataMapDto = new DataMapDto<>();
			typeMap.getBranch().put(typeId, dataMapDto);
		}
		return dataMapDto;
	}

	/**
	 *Removes all branches containing no data records in the Measurement level
	 */
	public void clean() {
		for (Map.Entry<String, DataMapDto<RecordDtoImpl>> stationEntry: this.getBranch().entrySet()) {
			Map<String, DataMapDto<RecordDtoImpl>> typeMap = stationEntry.getValue().getBranch();
			typeMap.entrySet().removeIf(entry -> entry.getValue().getData().isEmpty());
		}
		this.getBranch().entrySet().removeIf(entry -> entry.getValue().getBranch().isEmpty());
	}
	public String getProvenance() {
		return provenance;
	}
	public void setProvenance(String provenanceUuid) {
		this.provenance = provenanceUuid;
	}

	public static DataMapDto<RecordDtoImpl> build(String provenanceUuid, String stationCode, String dataType, List<RecordDtoImpl> values) {
		DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
		rootMap.setProvenance(provenanceUuid);
		DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(stationCode);
		DataMapDto<RecordDtoImpl> metricMap = stationMap.upsertBranch(dataType);
		metricMap.getData().addAll(values);
		return rootMap;
	}
}
