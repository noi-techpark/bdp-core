// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto;

@Deprecated
public class TextDto extends RecordDtoImpl{

	private static final long serialVersionUID = 8824412836092026904L;
	private String text;

	public TextDto() {
	}
	public TextDto(Long timestamp, String text) {
		super();
		this.timestamp = timestamp;
		this.text = text;
	}

	@Override
	public Object getValue() {
		return text;
	}
	public void setValue(Object value) {
		this.text = value.toString();
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
