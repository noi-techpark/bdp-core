package it.bz.idm.bdp.json;
import java.util.Date;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import it.bz.idm.bdp.DataPusher;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;


public abstract class JSONPusher extends DataPusher {
	private static final String SYNC_DATA_TYPES = "/syncDataTypes/";
	private static final String SYNC_STATIONS = "/syncStations/";
	private static final String PUSH_RECORDS = "/pushRecords/";
	private static final String GET_DATE_OF_LAST_RECORD = "/getDateOfLastRecord/";
	private static final String JSON_ENDPOINT = "json_endpoint";

	protected RestTemplate restTemplate = new RestTemplate();
	
	private String url;
	public JSONPusher() {
		this.url = "http://" + config.getString(HOST_KEY)+":"+config.getString(PORT_KEY)+config.getString(JSON_ENDPOINT);
	}
	@Override
	public Object pushData(String datasourceName, DataMapDto<? extends RecordDtoImpl> dto) {
		if (dto == null)
			return null;
		return restTemplate.postForObject(url + PUSH_RECORDS + "{datasourceName}", dto, Object.class, datasourceName);
	}
	
	
	public Object pushData(DataMapDto<? extends RecordDtoImpl> dto) {
		return pushData(this.integreenTypology, dto);
	}
	public void syncStations(StationList data) {
		this.syncStations(this.integreenTypology, data);
	}
	@Override
	public Object syncStations(String datasourceName, StationList data) {
		if (data == null)
			return null;
		return restTemplate.postForObject(url + SYNC_STATIONS + "{datasourceName}" , data, Object.class, datasourceName);
	}
	@Override
	public Object syncDataTypes(String datasourceName, List<DataTypeDto> data) {
		if (data == null)
			return null;
		return restTemplate.postForObject(url + SYNC_DATA_TYPES, data, Object.class);
	}
	public Object syncDataTypes(List<DataTypeDto> data) {
		return syncDataTypes(this.integreenTypology, data);
	}

	@Override
	public Object getDateOfLastRecord(String stationCode, String dataType, Integer period) {
		return restTemplate.getForObject(url + GET_DATE_OF_LAST_RECORD+"{datasourceName}/?stationId={stationId}&typeId={dataType}&period={period}", Date.class,this.integreenTypology, stationCode, dataType, period);
	}

	@Override
	public void connectToDataCenterCollector() {
		// TODO authentification to writer
	}
}
