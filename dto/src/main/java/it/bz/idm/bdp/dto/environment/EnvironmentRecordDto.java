package it.bz.idm.bdp.dto.environment;

import java.io.Serializable;
import java.util.Date;

public class EnvironmentRecordDto implements Serializable {
	private static final long serialVersionUID = 4851382811235801919L;
	private Date timestamp;
	private String value;
	public EnvironmentRecordDto() {
	}
	public EnvironmentRecordDto(Date timestamp, String value) {
		super();
		this.timestamp = timestamp;
		this.value = value;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}


}
