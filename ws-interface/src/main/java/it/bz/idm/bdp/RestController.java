package it.bz.idm.bdp;

import it.bz.idm.bdp.security.JwtUtil;
import it.bz.idm.bdp.util.IntegreenException;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public abstract class RestController {
	
	@Autowired
	public DataRetriever retriever;

	@Autowired
	private JwtUtil util;
	
	@Autowired
	public AuthenticationManager authenticationManager;
	
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
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pw));
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		return util.generateToken(principal);
	}
	@RequestMapping(value = "get-stations", method = RequestMethod.GET)
	public @ResponseBody String[] getStationIds() {
		return retriever.getStations();
	}

	@RequestMapping(value = "get-station-details", method = RequestMethod.GET)
	public @ResponseBody List<StationDto> getStationDetails() {
		return retriever.getStationDetails(null);
	}

	@RequestMapping(value = {"get-data-types"}, method = RequestMethod.GET)
	public @ResponseBody List<Object> getDataTypes(
			@RequestParam(value = "station", required = false) String station) {
		return retriever.getDataTypes(station);
	}
	
	@RequestMapping(value = {"get-records"}, method = RequestMethod.GET)
	public @ResponseBody List<RecordDto> getRecords(
			@RequestParam("station") String station,
			@RequestParam("name") String cname,
			@RequestParam("seconds") Long seconds,
			@RequestParam(value = "period", required = false) Integer period) {
		return retriever.getRecords(station, cname, seconds, period);
	}
	
	@RequestMapping(value = {"get-records-in-timeframe"}, method = RequestMethod.GET)
	public @ResponseBody List<RecordDto> getRecordsInTimeFrame(
			@RequestParam("station") String station,
			@RequestParam("name") String cname,
			@RequestParam("from") Long from, @RequestParam("to") Long to,
			@RequestParam(value = "period", required = false) Integer period) {
		return retriever.getRecords(station, cname, new Date(from),new Date(to), period);
	}

	@RequestMapping(value = {"get-date-of-last-record"}, method = RequestMethod.GET)
	public @ResponseBody Date getDateOfLastRecord(
			@RequestParam("station") String station,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="period",required=false) Integer period) {
		return retriever.getDateOfLastRecord(station, type,period);
	}
	@RequestMapping(value = {"get-newest-record"}, method = RequestMethod.GET)
	public @ResponseBody Object getNewestRecord(
			@RequestParam("station") String station,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="period",required=false) Integer period) {
		return retriever.getNewestRecord(station, type,period);
	}
}
