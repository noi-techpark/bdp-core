package it.bz.idm.bdp;

import it.bz.idm.bdp.dto.DataMapDto;

public interface IntegreenPushable {
	
	public abstract Object pushData(String datasourceName, DataMapDto dto);
	public abstract Object syncStations(String datasourceName, Object[] data);
	public abstract Object syncDataTypes(String datasourceName, Object[] data);
	public abstract Object getDateOfLastRecord(String stationCode,String dataType,Integer period);

}
