package it.bz.idm.bdp;

import java.util.List;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

public interface IntegreenPushable {
	
	public abstract <T> DataMapDto<RecordDtoImpl> mapData(T data);
	public abstract Object pushData(String datasourceName, DataMapDto<? extends RecordDtoImpl> dto);
	public abstract Object syncStations(String datasourceName, StationList dtos);
	public abstract Object syncDataTypes(String datasourceName,List<DataTypeDto> data);
	public abstract Object getDateOfLastRecord(String stationCode,String dataType,Integer period);
}
