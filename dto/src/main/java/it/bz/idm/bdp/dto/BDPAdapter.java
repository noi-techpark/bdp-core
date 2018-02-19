package it.bz.idm.bdp.dto;

import java.util.List;

public interface BDPAdapter {
	
	public abstract StationDto convert2StationDto(Object station);
	public abstract List<DataTypeDto> convert2DatatypeDtos(List<? extends Object> types);
	
}
