package it.bz.idm.bdp.dto;

public abstract class RecordDtoImpl implements RecordDto,Comparable<RecordDtoImpl>{

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

	public abstract Object getValue();

	public boolean validate() {
		return this.timestamp != null && this.getValue() != null;
	}
	
	@Override
	public int compareTo(RecordDtoImpl o) {
		return this.timestamp > o.timestamp ? 1:-1;
	}

	
}
