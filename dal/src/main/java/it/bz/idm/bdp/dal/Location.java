package it.bz.idm.bdp.dal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.vividsolutions.jts.geom.Geometry;

@Table(name = "location")
@Entity
public class Location {
	
	@Id
	@GeneratedValue(generator = "event_location_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "event_location__gen", sequenceName = "event_location__seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('event_location__seq')")
	protected Long id;

	protected Geometry geometry;
	
	@Lob
	private String description;

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
