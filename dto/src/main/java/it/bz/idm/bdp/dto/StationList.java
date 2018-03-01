package it.bz.idm.bdp.dto;

import java.util.ArrayList;
import java.util.Collection;

public class StationList extends ArrayList<StationDto>{

	public StationList(Collection<? extends StationDto> stations) {
		super(stations);
	}
	public StationList() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2408060694809964354L;

}
