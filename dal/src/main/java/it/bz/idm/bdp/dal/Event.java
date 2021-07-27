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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
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

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vladmihalcea.hibernate.type.range.Range;

import it.bz.idm.bdp.dto.EventDto;

@Table(name = "event", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid"}))
@Entity
public class Event {
	
	private static WKTReader wktReader = new WKTReader(Station.geometryFactory);
	
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
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
	
	@Lob
	private String description;

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

	public void setEventInterval(Range<LocalDateTime> eventInterval) {
		this.eventInterval = eventInterval;
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
		for (EventDto dto : eventDtos) {
			Event event = Event.find(dto.getId());
			if (event == null) { // avoid saving the same event multiple times
				event = new Event();
				event.setUuid(dto.getId());
				event.setCategory(dto.getCategory());
				event.setDescription(dto.getDescription());
				if (dto.getEventStart() != null && dto.getEventEnd() != null) {
					String rangeString = generateRangeString(dto);
					Range<LocalDateTime> eventInterval = Range.localDateTimeRange(rangeString);
					event.setEventInterval(eventInterval);
				}
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
				if (dto.getGeoJson() != null) {
					Location loc = new Location();
					try {
						loc.setGeometry(wktReader.read(dto.getGeoJson()));
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

	public static String generateRangeString(EventDto dto) {
		String eventStart = df.format(new Date(dto.getEventStart()));
		String eventEnd= df.format(new Date(dto.getEventEnd()));
		String rangeString = "["+eventStart+","+eventEnd+"]";
		return rangeString;
	}

	public static Event find(String id) {
		return null;
		
	}
}
