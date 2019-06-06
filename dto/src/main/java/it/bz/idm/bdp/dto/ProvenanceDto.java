package it.bz.idm.bdp.dto;

import java.io.Serializable;

public class ProvenanceDto implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 2627858984135614589L;

	protected String uuid;

	protected String lineage;

	protected String dataCollector;

	protected String dataCollectorVersion;

	public ProvenanceDto() {
	}
	public ProvenanceDto(String uuid, String dataCollector, String dataCollectorVersion, String lineage) {
		this.uuid=uuid;
		this.dataCollector = dataCollector;
		this.dataCollectorVersion = dataCollectorVersion;
		this.lineage = lineage;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLineage() {
		return lineage;
	}

	public void setLineage(String lineage) {
		this.lineage = lineage;
	}

	public String getDataCollector() {
		return dataCollector;
	}

	public void setDataCollector(String dataCollector) {
		this.dataCollector = dataCollector;
	}

	public String getDataCollectorVersion() {
		return dataCollectorVersion;
	}

	public void setDataCollectorVersion(String dataCollectorVersion) {
		this.dataCollectorVersion = dataCollectorVersion;
	}

}
