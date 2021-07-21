package it.bz.idm.bdp.dal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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

import com.vladmihalcea.hibernate.type.range.Range;

import it.bz.idm.bdp.dto.EventDto;

@Table(name = "event", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid"}))
@Entity
public class Event {
	
	
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
					metaData.getJson().putAll(dto.getMetaData());
					event.setMetaData(metaData);
				}
				if (dto.getGeoJson() != null) {
					Location loc = new Location();
					loc.setGeometry(dto.getGeoJson());
					loc.setDescription(dto.getLocationDescription());
					event.setLocation(loc);
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
