package it.bz.idm.bdp.ws;

import java.util.Date;
import java.util.List;

import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;
public interface IntegreenRunnable {

	public abstract String[] fetchStations();
	
	public abstract List<StationDto> fetchStationDetails(String stationId);

	public abstract List<List<String>> fetchDataTypes(String station);
	
	public abstract List<TypeDto> fetchTypes(String station);
	
	public abstract List<RecordDto> fetchRecords(String stationId, String typeId, Integer seconds, Integer period);
	public abstract List<RecordDto> fetchRecords(String stationId, String typeId, Long start, Long end, Integer period);
	
	public abstract RecordDto fetchNewestRecord(String stationId, String typeId, Integer period);
	
	public abstract Date fetchDateOfLastRecord(String stationId, String typeId, Integer period);
	
	public abstract List<? extends ChildDto> fetchChildStations(String id);

	public AccessTokenDto fetchAccessToken(String refreshToken);
	
	public JwtTokenDto fetchRefreshToken(String userName, String password);
}
