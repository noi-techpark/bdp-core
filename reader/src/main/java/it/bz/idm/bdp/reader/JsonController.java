package it.bz.idm.bdp.reader;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.reader.security.AccessToken;
import it.bz.idm.bdp.reader.security.JwtUtil;

@Controller
public class JsonController extends DataRetriever{
	
	@Autowired
	public AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil util;
	
	@RequestMapping(value = "refreshToken", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<JwtToken> getAccessToken(@RequestParam(value="user",required=true) String user,@RequestParam(value="password",required=true)String pw) {
		try {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pw));
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		JwtToken token = util.generateToken(principal);
		return new ResponseEntity<JwtToken>(token,HttpStatus.OK);
		}catch(BadCredentialsException bad) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
	@RequestMapping(value = "accessToken", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<AccessToken> getRefreshToken(HttpServletRequest request, Principal principal) {
		if (principal != null) {
			return new ResponseEntity<AccessToken>(util.generateAccessToken((UsernamePasswordAuthenticationToken) principal),HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}
	@RequestMapping(value = "/stations", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<String> getStations(@RequestParam String stationType) {
		return super.getStations(stationType);
	}
	@RequestMapping(value = "/link-stations", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<StationDto> getAvailableStations() {
		return super.getAvailableStations();
	}
	@RequestMapping(value = "/child-stations", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<ChildDto> getChildren(@RequestParam String stationType,@RequestParam String parent) {
		return super.getChildren(stationType, parent);
	}
	@RequestMapping(value = "/data-types", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<String[]> getDataTypes(@RequestParam String stationType, @RequestParam(required=false) String stationId) {
		return super.getDataTypes(stationType, stationId);
	}
	@RequestMapping(value = "/types", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<TypeDto> getTypes(String stationType, String stationId) {
		return super.getTypes(stationType, stationId);
	}
	@RequestMapping(value = "/date-of-last-record", method = RequestMethod.GET)
	@Override
	public @ResponseBody Date getDateOfLastRecord(@RequestParam String stationType,@RequestParam String stationId, @RequestParam(required=false) String typeId, @RequestParam(required=false) Integer period) {
		return super.getDateOfLastRecord(stationType, stationId, typeId, period);
	}
	@RequestMapping(value = "/last-record", method = RequestMethod.GET)
	@Override
	public @ResponseBody RecordDto getLastRecord(@RequestParam String stationType, @RequestParam String stationId, @RequestParam(required=false)String typeId, @RequestParam(required=false)Integer period) {
		return super.getLastRecord(stationType, stationId, typeId, period);
	}
	@RequestMapping(value = "/newest-record", method = RequestMethod.GET)
	@Override
	public @ResponseBody RecordDto getNewestRecord(@RequestParam String stationType,@RequestParam String stationId, @RequestParam(required=false) String typeId, @RequestParam(required=false) Integer period, Principal principal) {
		RecordDto newestRecord = super.getNewestRecord(stationType, stationId, typeId, period, principal);
		return newestRecord;
	}
	@RequestMapping(value = "/records", method = RequestMethod.GET)
	public @ResponseBody List<RecordDto> getRecords(@RequestParam String stationType,@RequestParam String stationId,@RequestParam(required=false) String typeId,@RequestParam(required=false) Long start,@RequestParam(required=false) Long end,
			@RequestParam(required=false) Integer period, @RequestParam(required=false)Integer seconds, Principal p) {
		return super.getRecords(stationType, stationId, typeId, start != null ? new Date(start) : null,
				end != null ? new Date(end) : null, period, seconds, p);
	}
	@RequestMapping(value = "/station-details", method = RequestMethod.GET)
	@Override
	public @ResponseBody  List<? extends StationDto> getStationDetails(@RequestParam String stationType,@RequestParam(required=false) String stationId) {
		List<? extends StationDto> stationDetails = super.getStationDetails(stationType, stationId);
		return stationDetails;
	}
}
