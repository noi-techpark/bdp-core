/**
 * reader - Data Reader for the Big Data Platform, that queries the database for web-services
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
package it.bz.idm.bdp.reader;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;
import it.bz.idm.bdp.reader.security.JwtUtil;

/**
 * TODO Please, describe it!
 *
 * @author Patrick Bertolla
 */
@Controller
public class JsonController extends DataRetriever{

	@Autowired
	public AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil util;


	@RequestMapping(value = "refreshToken", method = RequestMethod.GET)
	public @ResponseBody JwtTokenDto getAccessToken(@RequestParam(value="user", required=true) String user,
													@RequestParam(value="password", required=true) String pw) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user, pw));
		UserDetails principal = (UserDetails) authentication.getPrincipal();
		JwtTokenDto token = util.generateToken(principal);
		return token;
	}
	@RequestMapping(value = "accessToken", method = RequestMethod.GET)
	public @ResponseBody AccessTokenDto getRefreshToken(HttpServletRequest request, Principal principal) {
		if (principal != null) {
			return util.generateAccessToken((UsernamePasswordAuthenticationToken) principal);
		}
		return null;
	}
	@RequestMapping(value = "/stations", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<String> getStations(@RequestParam(required = false) String stationType) {
		if (stationType == null)
			return super.getStationTypes();
		return super.getStations(stationType);
	}
	@RequestMapping(value = "/data-types", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<String[]> getDataTypes(@RequestParam String stationType,
													 @RequestParam(required=false) String stationId) {
		return super.getDataTypes(stationType, stationId);
	}
	@RequestMapping(value = "/types", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<TypeDto> getTypes(@RequestParam String stationType,
												@RequestParam(required=false) String stationId) {
		return super.getTypes(stationType, stationId);
	}
	@RequestMapping(value = "/date-of-last-record", method = RequestMethod.GET)
	@Override
	public @ResponseBody Date getDateOfLastRecord(@RequestParam String stationType,
												  @RequestParam String stationId,
												  @RequestParam(required=false) String typeId,
												  @RequestParam(required=false) Integer period,
												  Principal principal) {
		return super.getDateOfLastRecord(stationType, stationId, typeId, period, principal);
	}
	@RequestMapping(value = "/last-record", method = RequestMethod.GET)
	@Override
	public @ResponseBody RecordDto getLastRecord(@RequestParam String stationType,
												 @RequestParam String stationId,
												 @RequestParam(required=false) String typeId,
												 @RequestParam(required=false) Integer period,
												 Principal principal) {
		return super.getLastRecord(stationType, stationId, typeId, period, principal);
	}
	@RequestMapping(value = "/newest-record", method = RequestMethod.GET)
	public @ResponseBody RecordDto getNewestRecord(@RequestParam String stationType,
												   @RequestParam String stationId,
												   @RequestParam(required=false) String typeId,
												   @RequestParam(required=false) Integer period,
												   Principal principal) {
		return super.getLastRecord(stationType, stationId, typeId, period, principal);
	}
	@RequestMapping(value = "/records", method = RequestMethod.GET)
	public @ResponseBody List<RecordDto> getRecords(@RequestParam String stationType,
													@RequestParam String stationId,
													@RequestParam(required=false) String typeId,
													@RequestParam(required=false) Long start,
													@RequestParam(required=false) Long end,
													@RequestParam(required=false) Integer period,
													@RequestParam(required=false) Integer seconds,
													Principal p) {
		return super.getRecords(stationType, stationId, typeId,
								start != null ? new Date(start) : null,
								end != null ? new Date(end) : null,
								period, seconds, p);
	}
	@RequestMapping(value = "/station-details", method = RequestMethod.GET)
	@Override
	public @ResponseBody  List<? extends StationDto> getStationDetails(@RequestParam String stationType,
																	   @RequestParam(required=false) String stationId) {
		List<? extends StationDto> stationDetails = super.getStationDetails(stationType, stationId);
		return stationDetails;
	}
}
