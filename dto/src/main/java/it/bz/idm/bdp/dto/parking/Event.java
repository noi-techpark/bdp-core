package it.bz.idm.bdp.dto.parking;

public class Event implements ObservationMetaInfo{
	
	private String description = "no event registered";
	
	public void setId(int id) {
		this.id = id;
	}

	private int id= -1;
	
	public Event(String descr, int id){
		this.description = descr;
		this.id = id;
	}
	
	public Event(){
		this.description = "";
		this.id = -1;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}
}
