package it.bz.idm.bdp;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;

public interface IntegreenPushable {
	
	public abstract <T> DataMapDto<RecordDtoImpl> parseData(T data);
	public abstract Object pushData(String datasourceName, DataMapDto<? extends RecordDtoImpl> dto);
	public abstract Object syncStations(String datasourceName, Object[] data);
	public abstract Object syncDataTypes(String datasourceName, Object[] data);
	public abstract Object getDateOfLastRecord(String stationCode,String dataType,Integer period);
}
