package it.bz.idm.bdp.ws;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

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

import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.ws.security.JwtUtil;
import it.bz.idm.bdp.ws.util.IntegreenException;

public abstract class RestControllerV2 {
	
	protected DataRetriever retriever;
	
	public abstract DataRetriever initDataRetriever();
	
	@Autowired
	private JwtUtil util;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
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
	/*
	 * @param user the user to auth with
	 */
	@RequestMapping(value = "auth-token", method = RequestMethod.GET)
	public @ResponseBody String token(@RequestParam(value="user",required=true) String user,@RequestParam(value="password",required=true)String pw) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pw));
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		return util.generateToken(principal);
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
