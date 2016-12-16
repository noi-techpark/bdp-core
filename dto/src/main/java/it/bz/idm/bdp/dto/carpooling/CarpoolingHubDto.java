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
