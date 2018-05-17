package it.bz.idm.bdp.dal.authentication;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Immutable;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Station;

@Immutable
@Entity
public class BDPPermissions {

	@Id
	private Long uuid;

	@ManyToOne
	private BDPRole role;

	@ManyToOne
	private Station station;

	@ManyToOne
	private DataType type;

	private Integer period;

	public BDPRole getRole() {
		return role;
	}
	public void setRole(BDPRole role) {
		this.role = role;
	}
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
}
