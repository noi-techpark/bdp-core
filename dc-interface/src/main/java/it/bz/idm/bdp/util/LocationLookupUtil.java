/**
 * dc-interface - Data Collector Interface for the Big Data Platform
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
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
package it.bz.idm.bdp.util;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client to reverse lookup a coordinate on Openstreetmap
 * @author Patrick Bertolla
 *
 */
public class LocationLookupUtil {

	private static final String NOMINATIM_SCHEME = "https";
	private static final String NOMINATIM_PATH = "/reverse";
	private static final String NOMINATIM_HOST = "nominatim.openstreetmap.org";
	protected RestTemplate restTemplate = new RestTemplate();
	private MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
	/**
	 * Municipalities are described in the response DTO address field in one of this fields. If the first is missing the second one is used and so on
	 */
	private String[] municipalityDenominators = new String[]{"city","town","village","hamlet"};

	public LocationLookupUtil() {
		super();
		this.uriVariables.add("format", "jsonv2");
	}
	/**
	 * @param longitude in EPSG 4326 projection
	 * @param latitude in EPSG 4326 projection
	 * @return municipality of the give coordinate, wit a fallback to hamlet
	 */
	public String lookupLocation(Double longitude, Double latitude) {
		if (longitude==null || latitude == null)
			throw new IllegalStateException("Missing parameter to reverse lookup location");
		uriVariables.add("lon", longitude.toString());
		uriVariables.add("lat", latitude.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.add("referer", "NOI-Techpark");
		headers.add("Content-Type", "application/json");
		headers.add("accept", "application/json");
		NominatimDto responseType = new NominatimDto();
		UriComponents uriComponents =
	            UriComponentsBuilder.newInstance()
	                .scheme(NOMINATIM_SCHEME).host(NOMINATIM_HOST).path(NOMINATIM_PATH).queryParams(uriVariables)
	                .build();
		URI uri = uriComponents.toUri();
		RequestEntity<NominatimDto> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);

		ResponseEntity<? extends NominatimDto> exchange = restTemplate.exchange(requestEntity,responseType.getClass());
		NominatimDto body = exchange.getBody();
		return extractMunicipality(body);
	}
	private String extractMunicipality(NominatimDto dto) {
		for (String key : municipalityDenominators) {
			String municipality = dto.getAddress().get(key);
			if (municipality != null)
				return municipality;
		}
		return null;
	}
}
