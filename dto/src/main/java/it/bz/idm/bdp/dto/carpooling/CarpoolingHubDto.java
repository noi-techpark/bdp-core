/**
 * dto - Data Transport Objects for an object-relational mapping
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.bz.idm.bdp.dto.carpooling;

import java.util.HashMap;
import java.util.Map;

import it.bz.idm.bdp.dto.StationDto;

public class CarpoolingHubDto extends StationDto{

	private static final long serialVersionUID = -7622213990063247812L;
	
	private Map<String,LocationTranslationDto> i18n = new HashMap<String, LocationTranslationDto>();

	public CarpoolingHubDto(String stationcode, String name, Double y, Double x, Map<String, LocationTranslationDto> translationDtos) {
		super(stationcode, name, y, x);
		this.i18n = translationDtos;
		
	}
	public CarpoolingHubDto() {
	}
	public Map<String, LocationTranslationDto> getI18n() {
		return i18n;
	}

	public void setI18n(Map<String, LocationTranslationDto> i18n) {
		this.i18n = i18n;
	}

}
