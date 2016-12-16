package it.bz.idm.bdp;


public interface IntegreenPushable {
	
	public abstract Object pushData(String datasourceName, Object[] data);
	public abstract Object syncStations(String datasourceName, Object[] data);
	public abstract Object syncDataTypes(String datasourceName, Object[] data);
	public abstract Object getDateOfLastRecord(String stationCode,String dataType,Integer period);

}
