package it.bz.idm.bdp.dal;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class M {

	private Date created_on;
	private Date timestamp;
	@ManyToOne(cascade=CascadeType.ALL)
	private Station station;

	@ManyToOne(cascade=CascadeType.PERSIST)
	private DataType type;

	private Integer period;
	
	public M() {
		this.created_on = new Date();
	}
	
	public M(Station station, DataType type, Date timestamp, Integer period) {
		this.station = station;
		this.type = type;
		this.timestamp = timestamp;
		this.period = period;
		this.created_on = new Date();
	}

	public Date getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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
	
	protected Date getDateOfLastRecordImpl(EntityManager em, Station station, DataType type, Integer period,
			BDPRole role, String table) {
		if (station == null)
			return null;

		if (!table.matches("[a-zA-Z_]+")) {
			throw new IllegalArgumentException("Table '" + table + "' contains illegal characters.");
		}

		String queryString = "select record.timestamp "
				+ "from " + table + " record, BDPPermissions p "
				+ "WHERE (record.station = p.station OR p.station = null) "
				+ "AND (record.type = p.type OR p.type = null) "
				+ "AND (record.period = p.period OR p.period = null) "
				+ "AND p.role = :role "
				+ "AND record.station=:station";

		if (type != null) {
			queryString += " AND record.type = :type";
		}
		if (period != null) {
			queryString += " AND record.period=:period";
		}
		queryString += " ORDER BY record.timestamp DESC";
		TypedQuery<Date> query = em.createQuery(queryString, Date.class);
		query.setParameter("station", station);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		if (type != null)
			query.setParameter("type", type);
		if (period != null)
			query.setParameter("period", period);
		return JPAUtil.getSingleResultOrAlternative(query, new Date(0));
	}
	public static <T> M findLatestEntry(EntityManager em, Station station, DataType type, Integer period, BDPRole role, String table) {
		if (station == null)
			return null;
		String baseQuery = "SELECT record FROM "+table+" record, BDPPermissions p"
						 + " WHERE (record.station = p.station OR p.station = null)"
						 + " AND (record.type = p.type OR p.type = null)"
						 + " AND (record.period = p.period OR p.period = null)"
					 	 + " AND p.role = :role "
					 	 + "AND record.station = :station";
		String order = " ORDER BY record.timestamp DESC";

		TypedQuery<? extends M> query = null;
		//set optional parameters
		if (type == null){
			if (period == null){
				query = em.createQuery(baseQuery + order, M.class);
			}else{
				query = em.createQuery(baseQuery + " AND record.period=:period" + order, M.class);
				query.setParameter("period", period);
			}
		}else if (period==null){
			query = em.createQuery(baseQuery + " AND record.type=:type" + order, M.class);
			query.setParameter("type", type);

		}else{
			query = em.createQuery(baseQuery + " AND record.type=:type AND record.period=:period" + order,
					M.class);
			query.setParameter("type", type);
			query.setParameter("period", period);
		}

		//set required paramaters
		query.setParameter("station", station);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		return JPAUtil.getSingleResultOrNull(query);
	}
	public abstract Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period, BDPRole role);
}