package it.bz.idm.bdp.util;

public interface LocationLookup {

	public String lookupLocation(Double longitude, Double latitude);
	public Double[] lookupCoordinates(String address);
}
