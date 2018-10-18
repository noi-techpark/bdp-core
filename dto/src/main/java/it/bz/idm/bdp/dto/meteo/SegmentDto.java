/**
 * dto - Data Transport Objects for an object-relational mapping
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
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
