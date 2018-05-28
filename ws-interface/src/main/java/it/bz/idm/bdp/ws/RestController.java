package it.bz.idm.bdp.ws;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.ws.util.DtoParser;
import it.bz.idm.bdp.ws.util.IntegreenException;

public abstract class RestController {
	
	protected DataRetriever retriever;
	
	public abstract DataRetriever initDataRetriever();
	
	@PostConstruct
	public void init(){
		this.retriever = initDataRetriever();
	}
	@ExceptionHandler(value = Throwable.class)
	public @ResponseBody ResponseEntity<IntegreenException> handleExceptions(
			Throwable exception) {
		IntegreenException integreenException = new IntegreenException(
				exception);
		HttpStatus statusError = HttpStatus.INTERNAL_SERVER_ERROR;
		if (exception instanceof ServletRequestBindingException)
			statusError = HttpStatus.BAD_REQUEST;
		return new ResponseEntity<IntegreenException>(integreenException,
				statusError);
	}
	
	@RequestMapping(value = "request-token", method = RequestMethod.GET)
	public @ResponseBody String getToken(@RequestParam(value="user",required=true) String user,@RequestParam(value="pw",required=true)String pw) {
		return null;
	}
	@RequestMapping(value = "access-token", method = RequestMethod.GET)
	public @ResponseBody AccessTokenDto getAccessToken(HttpServletRequest request) {
		return retriever.fetchAccessToken(request.getHeader(HttpHeaders.AUTHORIZATION));
	}
	@RequestMapping(value = "get-stations", method = RequestMethod.GET)
	public @ResponseBody String[] getStationIds() {
		return retriever.fetchStations();
	}

	@RequestMapping(value = "get-station-details", method = RequestMethod.GET)
	public @ResponseBody List<StationDto> getStationDetails() {
		return retriever.fetchStationDetails(null);
	}

	@RequestMapping(value = {"get-data-types"}, method = RequestMethod.GET)
	public @ResponseBody List<List<String>> getDataTypes(
			@RequestParam(value = "station", required = false) String station) {
		return retriever.fetchDataTypes(station);
	}
	
	@RequestMapping(value = {"get-records"}, method = RequestMethod.GET)
	public @ResponseBody List<SlimRecordDto> getRecords(
			@RequestParam("station") String station,
			@RequestParam("name") String cname,
			@RequestParam("seconds") Integer seconds,
			@RequestParam(value = "period", required = false) Integer period) {
		List<RecordDto> records = retriever.fetchRecords(station, cname, seconds, period);
		List<SlimRecordDto> list = DtoParser.reduce(records);
		return list;
	}
	
	@RequestMapping(value = {"get-records-in-timeframe"}, method = RequestMethod.GET)
	public @ResponseBody List<SlimRecordDto> getRecordsInTimeFrame(
			@RequestParam("station") String station,
			@RequestParam("name") String cname,
			@RequestParam("from") Long from, @RequestParam("to") Long to,
			@RequestParam(value = "period", required = false) Integer period) {
		List<RecordDto> records = retriever.fetchRecords(station, cname, from, to, period);
		List<SlimRecordDto> list = DtoParser.reduce(records);
		return list;
	}

	@RequestMapping(value = {"get-date-of-last-record"}, method = RequestMethod.GET)
	public @ResponseBody Date getDateOfLastRecord(
			@RequestParam("station") String station,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="period",required=false) Integer period) {
		return retriever.fetchDateOfLastRecord(station, type,period);
	}
	@RequestMapping(value = {"get-newest-record"}, method = RequestMethod.GET)
	public @ResponseBody RecordDto getNewestRecord(
			@RequestParam("station") String station,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="period",required=false) Integer period) {
		return retriever.fetchNewestRecord(station, type,period);
	}
}
