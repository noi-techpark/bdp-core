package it.bz.idm.bdp.dto.meteo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bz.idm.bdp.dto.meteo.SegmentDataPointDto;
public class SegmentDto implements Serializable{

	private static final long serialVersionUID = 1L;
	private Map<String, String> params = new HashMap<String, String>();
	private List<String> comments = new ArrayList<String>();
	private List<SegmentDataPointDto> dataPoints = new ArrayList<SegmentDataPointDto>();
	public SegmentDto() {
	}
	public SegmentDto(Map<String,String> params, List<String> comments,List<SegmentDataPointDto> dataPoints) {
		this.params = params;
		this.comments  = comments;
		this.dataPoints = dataPoints;
	}

	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public List<String> getComments() {
		return comments;
	}
	public void setComments(List<String> comments) {
		this.comments = comments;
	}
	public List<SegmentDataPointDto> getDataPoints() {
		return dataPoints;
	}
	public void setDataPoints(List<SegmentDataPointDto> dataPoints) {
		this.dataPoints = dataPoints;
	}
}
