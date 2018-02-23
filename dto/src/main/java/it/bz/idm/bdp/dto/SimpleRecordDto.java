package it.bz.idm.bdp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_EMPTY)
public class SimpleRecordDto extends RecordDtoImpl {

	/**
	 *
	 */
	private static final long serialVersionUID = 5703758724961079739L;
	private Object value;
	private Integer period;


	public SimpleRecordDto() {
	}
	public SimpleRecordDto(Long timestamp,Double value) {
		super();
		this.timestamp = timestamp;
		this.value = value;
	}
	public SimpleRecordDto(Long timestamp, Object value, Integer period) {
		super();
		this.timestamp = timestamp;
		this.value = value;
		this.period = period;
	}
	@Override
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	@JsonIgnore
	public boolean isValid() {
		return value!=null&&timestamp!=null&&period!=null;
	}

	@Override
	public String toString() {
		return "SimpleRecordDto [timestamp=" + timestamp + ", value=" + value + ", period=" + period + "]";
	}

}

