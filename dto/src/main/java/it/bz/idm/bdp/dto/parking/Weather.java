package it.bz.idm.bdp.dto.parking;

public class Weather implements ObservationMetaInfo{
	private String description = "no data available";
	private int id= -1;
	private int mintemp=-1;
	private int maxtemp = -1;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMintemp() {
		return mintemp;
	}
	public void setMintemp(int mintemp) {
		this.mintemp = mintemp;
	}
	public int getMaxtemp() {
		return maxtemp;
	}
	public void setMaxtemp(int maxtemp) {
		this.maxtemp = maxtemp;
	}
	
	

}
