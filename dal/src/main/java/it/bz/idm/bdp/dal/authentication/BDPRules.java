package it.bz.idm.bdp.dal.authentication;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Station;

@Entity
public class BDPRules {
    @Id
	@GeneratedValue(generator = "rules_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "rules_gen", sequenceName = "rules_seq", schema = "intime", allocationSize = 1)
    private Long id;

    @ManyToOne
	private BDPRole role;
    @ManyToOne
	private Station station;
    @ManyToOne
	private DataType type;
	private Integer period;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
