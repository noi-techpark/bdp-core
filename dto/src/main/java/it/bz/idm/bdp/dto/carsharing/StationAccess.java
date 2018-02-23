package it.bz.idm.bdp.dto.carsharing;

import java.io.Serializable;

public class StationAccess implements Serializable{
	private static final long serialVersionUID = -6882191851531656942L;
	String locationNote;
	   String parking;

	   public String getParking()
	   {
	      return this.parking;
	   }

	   public void setParking(String parking)
	   {
	      this.parking = parking;
	   }

	   public String getLocationNote()
	   {
	      return this.locationNote;
	   }

	   public void setLocationNote(String locationNote)
	   {
	      this.locationNote = locationNote;
	   }
}
