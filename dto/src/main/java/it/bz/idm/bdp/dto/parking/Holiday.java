package it.bz.idm.bdp.dto.parking;

public class Holiday implements ObservationMetaInfo {
	private String description = "no holiday registered";
	private int id= -1;

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
}
