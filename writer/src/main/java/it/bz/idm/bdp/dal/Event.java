/**
 * BDP data - Data Access Layer for the Big Data Platform
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
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
package it.bz.idm.bdp.dal;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.TypeDef;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vladmihalcea.hibernate.type.range.PostgreSQLRangeType;
import com.vladmihalcea.hibernate.type.range.Range;

import it.bz.idm.bdp.dal.util.QueryBuilder;
import it.bz.idm.bdp.dto.EventDto;

@Table(name = "event", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid"}))
@Entity
@TypeDef(
    typeClass = PostgreSQLRangeType.class,
    defaultForType = Range.class
)
public class Event {

	private static WKTReader wktReader = new WKTReader(Station.geometryFactory);

	@Id
	@GeneratedValue(generator = "event_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "event_gen", sequenceName = "event_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('event_seq')")
	protected Long id;

	private String uuid;
	private String category;

	private Date createdOn;

	private Range<LocalDateTime> eventInterval;

	@ManyToOne
	private Location location;

	@OneToOne
	private MetaData metaData;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private Provenance provenance;

	@Lob
	private String description;

	public Provenance getProvenance() {
		return provenance;
	}

	public void setProvenance(Provenance provenance) {
		this.provenance = provenance;
	}

	public Event() {
		setCreatedOn(new Date());
	}
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Range<LocalDateTime> getEventInterval() {
		return eventInterval;
	}

	/**
	 * Set the event interval as follows:
	 *
	 * <code>[lower-bound, upper-bound)</code>
	 *
	 * That is, a half-open interval, which is useful for moving window aggregates,
	 * because each window does not overlap with the next, which might happen
	 * with a closed interval on boundaries. It is possible that we have unbound
	 * limits, that is, they are infinite.
	 *
	 * @param eventInterval
	 */
	public void setEventInterval(Range<LocalDateTime> eventInterval) {
		if (
			(eventInterval.isLowerBoundClosed() || eventInterval.hasMask(Range.LOWER_INFINITE)) &&
			(!eventInterval.isUpperBoundClosed() || eventInterval.hasMask(Range.UPPER_INFINITE))
		) {
			this.eventInterval = eventInterval;
		}
		throw new IllegalArgumentException("The interval must be half-open [a,b) or unbounded.");
	}

	public void setEventInterval(final String eventInterval) {
		setEventInterval(Range.localDateTimeRange(eventInterval));
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public static void add(EntityManager em, List<EventDto> eventDtos) {
		if (eventDtos == null || eventDtos.isEmpty()) {
			return;
		}
		Provenance provenance = Provenance.findByUuid(em, eventDtos.get(0).getProvenance());
		for (EventDto dto : eventDtos) {
			Event event = Event.find(em, dto.getId());
			if (event == null) { // avoid saving the same event multiple times
				event = new Event();
				event.setUuid(dto.getId());
				event.setCategory(dto.getCategory());
				event.setDescription(dto.getDescription());
				event.setProvenance(provenance);
				event.setEventInterval(dto.getEventIntervalAsString());
				if (!dto.getMetaData().isEmpty()) {
					MetaData metaData = new MetaData();
					Map<String, Object> json = new HashMap<>();
					if (!json.equals(dto.getMetaData())) {
						json.putAll(dto.getMetaData());
						metaData.setJson(json);
						event.setMetaData(metaData);
						em.persist(metaData);
					}
				}
				if (dto.getWktGeometry() != null) {
					Location loc = new Location();
					try {
						loc.setGeometry(wktReader.read(dto.getWktGeometry()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					loc.setDescription(dto.getLocationDescription());
					event.setLocation(loc);
					em.persist(loc);
				}
				em.persist(event);
			}
		}
	}

	public static Event find(EntityManager em, String uuid) {
		return QueryBuilder
			.init(em)
			.addSql("SELECT e FROM Event e where 1=1")
			.setParameterIfNotNull("uuid", uuid, "and uuid = :uuid")
			.buildSingleResultOrNull(Event.class);
	}

}
