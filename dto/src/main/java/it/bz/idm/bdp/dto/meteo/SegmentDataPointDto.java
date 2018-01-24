package it.bz.idm.bdp.dto.meteo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SegmentDataPointDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss",Locale.ITALIAN); 

	private Date date;
	private String comment;
	private Double value;
	
	public SegmentDataPointDto() {
	}
	public SegmentDataPointDto(Double value, String comment, String date) throws ParseException {
		this.value = value;
		this.comment = comment;
		this.date = formatter.parse(date);
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
}
