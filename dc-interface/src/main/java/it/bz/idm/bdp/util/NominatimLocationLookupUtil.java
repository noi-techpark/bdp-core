// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.util;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client to reverse lookup a coordinate on OpenStreetMap
 *
 * @author Patrick Bertolla
 */
public class NominatimLocationLookupUtil implements LocationLookup{

	private static final String CONTENT_TYPE = "application/json";
	private static final String REFERER = "NOI-Techpark";
	private static final String NOMINATIM_SCHEME = "https";
	private static final String NOMINATIM_PATH = "/reverse";
	private static final String NOMINATIM_HOST = "nominatim.openstreetmap.org";
	protected RestTemplate restTemplate = new RestTemplate();
	private MultiValueMap<String, String> defaultUriVariables = new LinkedMultiValueMap<>();
	/**
	 * Municipalities are described in the response DTO address field in one of this fields. If the first is missing the second one is used and so on
	 */
	private String[] municipalityDenominators = new String[]{"city","town","village","hamlet"};

	public NominatimLocationLookupUtil() {
		super();
		this.defaultUriVariables.add("format", "jsonv2");
	}
	/**
	 * @param longitude in EPSG 4326 projection
	 * @param latitude in EPSG 4326 projection
	 * @return municipality of the give coordinate, wit a fallback to hamlet
	 * @throws NominatimException if nominatim is not available
	 */
	@Override
	public String lookupLocation(Double longitude, Double latitude) throws NominatimException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (longitude==null || latitude == null)
			throw new IllegalStateException("Missing parameter to reverse lookup location");
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("lon", longitude.toString());
		parameters.add("lat", latitude.toString());
		parameters.addAll(defaultUriVariables);
		HttpHeaders headers = new HttpHeaders();
		headers.add("referer", REFERER);
		headers.add("Content-Type", CONTENT_TYPE);
		headers.add("accept", CONTENT_TYPE);
		NominatimDto responseType = new NominatimDto();
		UriComponents uriComponents =
	            UriComponentsBuilder.newInstance()
	                .scheme(NOMINATIM_SCHEME).host(NOMINATIM_HOST).path(NOMINATIM_PATH).queryParams(parameters)
	                .build();
		URI uri = uriComponents.toUri();
		RequestEntity<NominatimDto> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);

		ResponseEntity<? extends NominatimDto> exchange = restTemplate.exchange(requestEntity,responseType.getClass());
		if (exchange.getStatusCode().equals(HttpStatus.BAD_GATEWAY))
			throw new NominatimException("Nominatim got to many request by" + REFERER);
		NominatimDto body = exchange.getBody();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return extractMunicipality(body);
	}
	/**
	 * @param address of the location you would like to find
	 * @return coordinate pair in the order longitude,latitude in EPSG 4326 projection
	 * @throws NominatimException
	 */
	@Override
	public Double[] lookupCoordinates(String address) throws NominatimException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (address==null)
			throw new IllegalStateException("Missing parameter to lookup position");
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("q", address.trim());
		parameters.addAll(defaultUriVariables);
		HttpHeaders headers = new HttpHeaders();
		headers.add("referer", REFERER);
		headers.add("Content-Type", CONTENT_TYPE);
		headers.add("accept", CONTENT_TYPE);
		UriComponents uriComponents =
	            UriComponentsBuilder.newInstance()
	                .scheme(NOMINATIM_SCHEME).host(NOMINATIM_HOST).path("/").queryParams(parameters)
	                .build();
		URI uri = uriComponents.toUri();
		RequestEntity<List<NominatimAddressLookupResponseDto>> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);

		ResponseEntity<NominatimAddressLookupResponseDto[]> exchange = restTemplate.exchange(requestEntity,NominatimAddressLookupResponseDto[].class);
		if (exchange.getStatusCode().equals(HttpStatus.BAD_GATEWAY))
			throw new NominatimException("Nominatim got to many request by" + REFERER);
		NominatimAddressLookupResponseDto[] body = exchange.getBody();
		if (body.length<=0)
			throw new IllegalStateException("could not find any coordinates for this string");
		Double longitude = Double.parseDouble(body[0].getLon());
		Double latitude = Double.parseDouble(body[0].getLat());
		return new Double[] {longitude,latitude};
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
