package it.bz.idm.bdp.dto;

import it.bz.idm.bdp.dto.RecordDto;

public abstract class RecordDtoImpl implements RecordDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1124149647267291299L;
	protected Long timestamp;
	
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public abstract <T extends Object> T getValue();

	public boolean validate() {
		return this.timestamp != null && this.getValue() != null;
	}
}
