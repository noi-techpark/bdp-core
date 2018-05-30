package it.bz.idm.bdp.ws;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;

public abstract class RestControllerV2 {
	
	protected DataRetriever retriever;
	
	public abstract DataRetriever initDataRetriever();
	
	@PostConstruct
	public void init(){
		this.retriever = initDataRetriever();
	}
	/*
	 * @param user the user to auth with
	 */
	@RequestMapping(value = "refresh-token", method = RequestMethod.GET)
	public @ResponseBody JwtTokenDto getToken(@RequestParam(value="user",required=true) String user,@RequestParam(value="pw",required=true)String pw) {
		return retriever.fetchRefreshToken(user, pw);
	}
	@RequestMapping(value = "access-token", method = RequestMethod.GET)
	public @ResponseBody AccessTokenDto getAccessToken(HttpServletRequest request) {
		return retriever.fetchAccessToken(request.getHeader(HttpHeaders.AUTHORIZATION));
	}
	@RequestMapping(value = "station-ids", method = RequestMethod.GET)
	public @ResponseBody String[] stationIds(HttpServletResponse response) {
		return retriever.fetchStations();
	}

	@RequestMapping(value = "station-details", method = RequestMethod.GET)
	public @ResponseBody List<StationDto> stationDetails(@RequestParam(required=false,value="station-id") String id) {
		List<it.bz.idm.bdp.dto.StationDto> stationDetails = retriever.fetchStationDetails(id);
		return stationDetails;
	}

	@RequestMapping(value = {"types"}, method = RequestMethod.GET)
	public @ResponseBody List<TypeDto> dataTypes(
			@RequestParam(value = "station", required = false) String station) {
			List<TypeDto> dataTypes = (List<TypeDto>) retriever.fetchTypes(station);
			return dataTypes;
	}
	
	@RequestMapping(value = {"history"}, method = RequestMethod.GET)
	public @ResponseBody List<RecordDto> history(
			@RequestParam("station") String station,
			@RequestParam("type") String cname,
			@RequestParam("seconds") Integer seconds,
			@RequestParam(value = "period", required = false) Integer period) {
		return retriever.fetchRecords(station, cname, seconds, period);
	}
	
	@RequestMapping(value = {"records"}, method = RequestMethod.GET)
	public @ResponseBody List<RecordDto> records(
			@RequestParam("station") String station,
			@RequestParam("type") String cname,
			@RequestParam("from") Long from, @RequestParam("to") Long to,
			@RequestParam(value = "period", required = false) Integer period) {
		return retriever.fetchRecords(station, cname, from, to, period);
	}

	@RequestMapping(value = {"newest"}, method = RequestMethod.GET)
	public @ResponseBody RecordDto newestRecord(
			@RequestParam("station") String station,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="period",required=false) Integer period) {
		return (RecordDto) retriever.fetchNewestRecord(station,type,period);
	}

}
