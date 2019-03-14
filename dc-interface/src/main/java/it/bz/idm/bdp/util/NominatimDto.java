package it.bz.idm.bdp.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Minimal implementation of the dto returned by Nominatim
 * @author Patrick Bertolla
 *
 */
public class NominatimDto implements Serializable{

	private static final long serialVersionUID = -3369918936655035312L;

	/**
	 * containing multiple fields describing the coordinate location
	 */
	private Map<String,String> address = new HashMap<>();

	public Map<String, String> getAddress() {
		return address;
	}

	public void setAddress(Map<String, String> address) {
		this.address = address;
	}
}
