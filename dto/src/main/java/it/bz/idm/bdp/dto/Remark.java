package it.bz.idm.bdp.dto;


import java.io.Serializable;


public class Remark implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String stationCode;
    protected String elementCode;
    protected Long date;
    protected Double value;

    public Remark(String stationCode, String elementCode, Long date, Double value) {
		this.stationCode = stationCode;
		this.elementCode = elementCode;
		this.date = date;
		this.value = value;
	}
	public String getStationCode() {
        return stationCode;
    }
    public void setStationCode(String value) {
        this.stationCode = value;
    }
    public String getElementCode() {
        return elementCode;
    }

    public void setElementCode(String value) {
        this.elementCode = value;
    }
    public Long getDate() {
        return date;
    }
    public void setDate(Long value) {
        this.date = value;
    }
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}



}
