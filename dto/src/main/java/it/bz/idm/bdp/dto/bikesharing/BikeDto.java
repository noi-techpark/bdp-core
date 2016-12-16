package it.bz.idm.bdp.dto.bikesharing;

import it.bz.idm.bdp.dto.ChildDto;

public class BikeDto extends ChildDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4658247063103057912L;
	private String type;


	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
