package it.bz.idm.bdp.ws;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import reactor.core.publisher.Mono;

public abstract class RestClient extends DataRetriever{

	protected WebClient webClient;
	private String url;

	
	@Override
	public String[] fetchStations() {
		Map<String,String> params = new HashMap<>();
		params.put("stationType", this.integreenTypology);
		Mono<String[]> body = webClient.get().uri("/stations?stationType={stationType}", params).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String[].class);
		return body.block();
	}

	@Override
	public List<StationDto> fetchStationDetails(String stationId) {
		Map<String,String> params = new HashMap<>();
		params.put("stationType", this.integreenTypology);
		params.put("stationId", stationId);
		Mono<List<StationDto>> mono = webClient.get().uri("/station-details/?stationType={stationType}&stationId={stationId}",params).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(new ParameterizedTypeReference<List<StationDto>> () {});
		return mono.block();
	}

	@Override
	public List<List<String>> fetchDataTypes(String stationId) {
		Map<String,String> params = new HashMap<>();
		params.put("stationType", this.integreenTypology);
		params.put("stationId", stationId);
		Mono<List<List<String>>> mono = webClient.get().uri("/data-types/?stationType={stationType}&stationId={stationId}",params).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(new ParameterizedTypeReference<List<List<String>>>() {});
		return mono.block();
		
	}

	@Override
	public List<TypeDto> fetchTypes(String station) {
		Map<String,String> params = new HashMap<>();
		params.put("stationType", this.integreenTypology);
		params.put("stationId", station);
		Mono<List<TypeDto>> response = webClient.get().uri("/types/?stationType={stationType}&stationId={stationId}",params).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(new ParameterizedTypeReference<List<TypeDto>>() {});
		return response.block();
	}

	@Override
	public List<RecordDto> fetchRecords(String stationId, String typeId, Integer seconds, Integer period) {
		Map<String,String> map = new HashMap<>();
		map.put("stationType", this.integreenTypology);
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("seconds", seconds != null ? String.valueOf(seconds) : null);
		map.put("period", period!=null?String.valueOf(period):null);
		Mono<List<RecordDto>> mono = webClient.get().uri("/records/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}&seconds={seconds}",map).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(new ParameterizedTypeReference<List<RecordDto>>() {});
		return mono.block();
	}
	@Override
	public List<RecordDto> fetchRecords(String stationId, String typeId, Long start,Long end, Integer period) {
		Map<String,String> map = new HashMap<>();
		map.put("stationType", this.integreenTypology);
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("start", start!=null ? String.valueOf(start) : null);
		map.put("end", end != null ? String.valueOf(end) : null);
		map.put("period", period!=null?String.valueOf(period):null);
		Mono<List<RecordDto>> mono = webClient.get().uri("/records/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}&start={start}&end={end}",map).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(new ParameterizedTypeReference<List<RecordDto>>() {});
		return mono.block();
	}

	@Override
	public RecordDto fetchNewestRecord(String stationId, String typeId, Integer period) {
		Map<String,String> map = new HashMap<>();
		map.put("stationType", this.integreenTypology);
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("period", period!=null?String.valueOf(period):null);
		Mono<RecordDtoImpl> mono = webClient.get().uri("/newest-record/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}",map).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(new ParameterizedTypeReference<RecordDtoImpl>() {});
		return mono.block();
	}

	@Override
	public Date fetchDateOfLastRecord(String stationId, String typeId, Integer period) {
		Map<String,String> map = new HashMap<>();
		map.put("stationType", this.integreenTypology);
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("period", String.valueOf(period));
		Mono<Date> mono = webClient.get().uri("/date-of-last-record/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}",map).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(new ParameterizedTypeReference<Date>() {});
		return mono.block();
	}

	@Override
	public List<? extends ChildDto> fetchChildStations(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connect() {
		String sslString = DEFAULT_SSL?"https":"http";
		this.url = sslString+"://"+DEFAULT_HOST+":"+DEFAULT_PORT+DEFAULT_ENDPOINT;
		webClient = WebClient.create(url);
	}

}
