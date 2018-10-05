package it.bz.idm.bdp.dal;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

@TypeDefs({ @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
@Entity
@Table(name = "metadata")
public class MetaData {

	@Id
	@GeneratedValue(generator = "metadata_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "metadata_gen", sequenceName = "metadata_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.metadata_seq')")
	protected Long id;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> json;

	@ManyToOne
	private Station station;

	private Date created_on;

	public MetaData() {
		created_on = new Date();
	}

	public Map<String, Object> getJson() {
		return json;
	}

	public void setJson(Map<String, Object> metaData) {
		this.json = metaData;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public Date getCreated() {
		return created_on;
	}
}
