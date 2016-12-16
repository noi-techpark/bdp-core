package it.bz.idm.bdp;

import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;

import java.util.Date;
import java.util.List;
public interface IntegreenRunnable {

	public abstract String[] getStations();
	
	public abstract List<StationDto> getStationDetails(String stationId);

	public abstract List<Object> getDataTypes(String station);
	
	public abstract List<? extends Object> getTypes(String station);
	
	public abstract List<RecordDto> getRecords(Object... objects);
	
	public abstract Object getNewestRecord(Object... objects);
	
	public abstract Date getDateOfLastRecord(Object... stationcode);

}
