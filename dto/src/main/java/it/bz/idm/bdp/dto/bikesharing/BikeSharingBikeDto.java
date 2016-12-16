package it.bz.idm.bdp.dto.bikesharing;

import it.bz.idm.bdp.dto.StationDto;



public class BikeSharingBikeDto extends StationDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 902769333357686586L;

	/**
	 * 
	 */
	
	private String parentStation;
	private String type;
	private Integer currentState;
	private Integer inStoreHouse;
	public BikeSharingBikeDto() {
	}

	public BikeSharingBikeDto(String bikecode, String stationCode,String name,Integer state,Integer inStoreHouse,  String type) {
		super(bikecode, name, null, null);
		this.parentStation =stationCode;
		this.type = type;
		this.inStoreHouse = inStoreHouse;
		this.currentState = state;
	}

	public BikeSharingBikeDto(String stationcode, String bikesharingCode,
			String name, String type) {
		super(bikesharingCode,name, null, null);
		this.parentStation =stationcode;
		this.type = type;
	}

	public String getParentStation() {
		return parentStation;
	}

	public void setParentStation(String parentStation) {
		this.parentStation = parentStation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getCurrentState() {
		return currentState;
	}

	public void setCurrentState(Integer currentState) {
		this.currentState = currentState;
	}

	public Integer getInStoreHouse() {
		return inStoreHouse;
	}

	public void setInStoreHouse(Integer inStoreHouse) {
		this.inStoreHouse = inStoreHouse;
	}
	
	
}
