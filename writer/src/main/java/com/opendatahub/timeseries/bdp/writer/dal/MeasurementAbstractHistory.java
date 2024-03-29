// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.writer.dal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendatahub.timeseries.bdp.writer.dal.util.JPAException;
import com.opendatahub.timeseries.bdp.writer.dal.util.Log;
import com.opendatahub.timeseries.bdp.writer.dal.util.QueryBuilder;
import com.opendatahub.timeseries.bdp.dto.dto.DataMapDto;
import com.opendatahub.timeseries.bdp.dto.dto.RecordDto;
import com.opendatahub.timeseries.bdp.dto.dto.RecordDtoImpl;
import com.opendatahub.timeseries.bdp.dto.dto.SimpleRecordDto;

/**
 * <p>This entity contains all measurements and is the biggest container for the data.
 * Each measurement <strong>must</strong> extend this base class to keep integrity.
 * It contains the two most important references to station and type and also contains generic
 * methods on how data gets stored and retrieved.
 *
 * @author Peter Moser
 * @author Patrick Bertolla
 */
@MappedSuperclass
public abstract class MeasurementAbstractHistory implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(MeasurementAbstractHistory.class);

    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private Date created_on;

    @Column(nullable = false)
    private Date timestamp;

    @ManyToOne(optional = false)
    private Station station;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private DataType type;

    @Column(nullable = false)
    private Integer period;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Provenance provenance;

    public abstract List<RecordDto> findRecords(EntityManager em, String stationtype, String identifier, String cname, Date start, Date end, Integer period);

    protected MeasurementAbstractHistory() {
        this.created_on = new Date();
    }
    /**
     * @param station entity the measurement refers to
     * @param type entity the measurement refers to
     * @param timestamp UTC time of the measurement detection
     * @param period standard interval between 2 measurements
     */
    protected MeasurementAbstractHistory(Station station, DataType type, Date timestamp, Integer period) {
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

    public Provenance getProvenance() {
        return provenance;
    }

    public void setProvenance(Provenance provenance) {
        this.provenance = provenance;
    }
    public abstract void setValue(Object value);
    public abstract Object getValue();

    /**
     * <p>
     * persists all measurement data send to the writer from data collectors to the database.<br/>
     * This method goes down the data tree and persists all new records<br/>
     * it also updates the newest measurement in {@link MeasurementAbstract}, if it really is newer
     * </p>
     * @param em entity manager
     * @param stationType typology of the specific station, e.g., MeteoStation, EnvironmentStation
     * @param dataMap  container for data send from data collector containing measurements<br/>
     * Data is received in a tree structure, containing in the first level the identifier of the correlated station,<br/>
     * on the second level the identifier of the correlated data type and on the last level the data itself
     * @throws JPAException if data is in any way corrupted or one of the references {@link Station}, {@link DataType}<br/> does not exist in the database yet
     */
    public static void pushRecords(EntityManager em, String stationType, DataMapDto<RecordDtoImpl> dataMap) {
		Log log = new Log(LOG, "pushRecords");
        try {
            Provenance provenance = Provenance.findByUuid(em, dataMap.getProvenance());
			if (provenance == null) {
				throw new JPAException(String.format("Provenance with UUID %s not found", dataMap.getProvenance()));
			}
			log.setProvenance(provenance);
            for (Entry<String, DataMapDto<RecordDtoImpl>> stationEntry : dataMap.getBranch().entrySet()) {
                Station station = Station.findStation(em, stationType, stationEntry.getKey());
                if (station == null) {
					log.warn(String.format("Station '%s/%s' not found. Skipping...", stationType, stationEntry.getKey()));
                    continue;
                }
                for(Entry<String,DataMapDto<RecordDtoImpl>> typeEntry : stationEntry.getValue().getBranch().entrySet()) {
                    try {
						DataType type = DataType.findByCname(em, typeEntry.getKey());
						if (type == null) {
							log.warn(String.format("Type '%s' not found. Skipping...", typeEntry.getKey()));
							continue;
						}
                        List<? extends RecordDtoImpl> dataRecords = typeEntry.getValue().getData();
                        if (dataRecords.isEmpty()) {
							log.warn("Empty data set. Skipping...");
                            continue;
                        }

						//TODO: remove period check once it gets removed from database
						Integer period = ((SimpleRecordDto) dataRecords.get(0)).getPeriod();
						if (period == null){
							log.warn("No period specified. Skipping...");
							continue;
						}

						em.getTransaction().begin();

                        MeasurementAbstract latestNumberMeasurement = MeasurementAbstract.findLatestEntry(em, station, type, period, Measurement.class);
                        long latestNumberMeasurementTime = (latestNumberMeasurement != null) ? latestNumberMeasurement.getTimestamp().getTime() : 0;
                        MeasurementAbstract latestStringMeasurement = MeasurementAbstract.findLatestEntry(em, station, type, period, MeasurementString.class);
                        long latestStringMeasurementTime = (latestStringMeasurement != null) ? latestStringMeasurement.getTimestamp().getTime() : 0;
                        MeasurementAbstract latestJSONMeasurement = MeasurementAbstract.findLatestEntry(em, station, type, period, MeasurementJSON.class);
                        long latestJSONMeasurementTime = (latestJSONMeasurement != null) ? latestJSONMeasurement.getTimestamp().getTime() : 0;

                        SimpleRecordDto newestStringDto = null;
                        SimpleRecordDto newestNumberDto = null;
                        SimpleRecordDto newestJsonDto = null;

						List<String> dupList = new ArrayList<>();

                        for (RecordDtoImpl recordDto : dataRecords) {

                            /*
                             * XXX We support only SimpleRecordDtos at the moment. This should be removed,
                             * when we see that we do not need anything else then SimpleRecords
                             */
                            if (! (recordDto instanceof SimpleRecordDto))
                                continue;

                            SimpleRecordDto simpleRecordDto = (SimpleRecordDto)recordDto;
                            Long dateOfMeasurement = simpleRecordDto.getTimestamp();
                            Object valueObj = simpleRecordDto.getValue();
                            if (valueObj instanceof Number) {
								if (latestNumberMeasurementTime < dateOfMeasurement) {
                                    Double value = ((Number)valueObj).doubleValue();
									if (isDuplicate(log, dupList, station, type, simpleRecordDto.getPeriod(), "NUMBER", dateOfMeasurement, value))
										continue;
                                    MeasurementHistory rec = new MeasurementHistory(station, type, value, new Date(dateOfMeasurement), simpleRecordDto.getPeriod());
                                    rec.setProvenance(provenance);
                                    em.persist(rec);
                                }
                                if (newestNumberDto == null || newestNumberDto.getTimestamp() < simpleRecordDto.getTimestamp()) {
                                    newestNumberDto = simpleRecordDto;
                                }
                            } else if (valueObj instanceof String) {
                                if (latestStringMeasurementTime < dateOfMeasurement) {
                                    String value = (String) valueObj;
									if (isDuplicate(log, dupList, station, type, simpleRecordDto.getPeriod(), "STRING", dateOfMeasurement, value))
										continue;
                                    MeasurementStringHistory rec = new MeasurementStringHistory(station, type, value, new Date(dateOfMeasurement), simpleRecordDto.getPeriod());
                                    rec.setProvenance(provenance);
                                    em.persist(rec);
                                }
                                if (newestStringDto == null || newestStringDto.getTimestamp() < simpleRecordDto.getTimestamp()) {
                                    newestStringDto = simpleRecordDto;
                                }
                            } else if (valueObj instanceof Map) {
                                if (latestJSONMeasurementTime < dateOfMeasurement) {
									@SuppressWarnings("unchecked")
                                    Map<String,Object> value = (Map<String,Object>) valueObj;
									if (isDuplicate(log, dupList, station, type, simpleRecordDto.getPeriod(), "JSON", dateOfMeasurement, value))
										continue;
                                    MeasurementJSONHistory rec = new MeasurementJSONHistory(station, type, value, new Date(dateOfMeasurement), simpleRecordDto.getPeriod());
                                    rec.setProvenance(provenance);
                                    em.persist(rec);
                                }
                                if (newestJsonDto == null || newestJsonDto.getTimestamp() < simpleRecordDto.getTimestamp()) {
                                    newestJsonDto = simpleRecordDto;
                                }
                            } else {
								log.warn(
									String.format("Unsupported data format for %s/%s/%s with value '%s'. Skipping...",
										stationType,
										stationEntry.getKey(),
										typeEntry.getKey(),
										(valueObj == null ? "(null)" : valueObj.getClass().getSimpleName())
									)
								);
                            }
                        }

                        if (newestNumberDto != null) {
                            Double valueNumber = ((Number)newestNumberDto.getValue()).doubleValue();
                            if (latestNumberMeasurement == null) {
                                latestNumberMeasurement = new Measurement(station, type, valueNumber, new Date(newestNumberDto.getTimestamp()), newestNumberDto.getPeriod());
                                latestNumberMeasurement.setProvenance(provenance);
                                em.persist(latestNumberMeasurement);
                            } else if (newestNumberDto.getTimestamp() > latestNumberMeasurementTime) {
                                latestNumberMeasurement.setTimestamp(new Date(newestNumberDto.getTimestamp()));
                                latestNumberMeasurement.setProvenance(provenance);
                                latestNumberMeasurement.setValue(valueNumber);
                                em.merge(latestNumberMeasurement);
                            }
                        }

                        if (newestStringDto != null) {
                            String valueString = (String) newestStringDto.getValue();
                            if (latestStringMeasurement == null) {
                                latestStringMeasurement = new MeasurementString(station, type, valueString, new Date(newestStringDto.getTimestamp()), newestStringDto.getPeriod());
                                latestStringMeasurement.setProvenance(provenance);
                                em.persist(latestStringMeasurement);
                            } else if (newestStringDto.getTimestamp() > latestStringMeasurementTime) {
                                latestStringMeasurement.setTimestamp(new Date(newestStringDto.getTimestamp()));
                                latestStringMeasurement.setValue(valueString);
                                latestStringMeasurement.setProvenance(provenance);
                                em.merge(latestStringMeasurement);
                            }
                        }
                        if (newestJsonDto != null) {
							@SuppressWarnings("unchecked")
                            Map<String,Object> jsonValue = (Map<String,Object>) newestJsonDto.getValue();
                            if (latestJSONMeasurement == null) {
                                latestJSONMeasurement = new MeasurementJSON(station, type, jsonValue, new Date(newestJsonDto.getTimestamp()), newestJsonDto.getPeriod());
                                latestJSONMeasurement.setProvenance(provenance);
                                em.persist(latestJSONMeasurement);
                            } else if (newestJsonDto.getTimestamp() > latestJSONMeasurementTime) {
                                latestJSONMeasurement.setTimestamp(new Date(newestJsonDto.getTimestamp()));
                                latestJSONMeasurement.setValue(jsonValue);
                                latestJSONMeasurement.setProvenance(provenance);
                                em.merge(latestJSONMeasurement);
                            }
                        }
						em.getTransaction().commit();

                    } catch(Exception ex) {
						log.error(
							String.format("Exception '%s'... Skipping this measurement!", ex.getMessage()),
							ex
						);
						if (em.getTransaction().isActive()){
                            em.getTransaction().rollback();
                        }
                        LOG.debug("Printing stack trace", ex);
                    }
                }
            }
        } catch(Exception e) {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
            throw JPAException.unnest(e);
        } finally {
            em.clear();
			if (em.isOpen())
                em.close();
        }
    }

	private static boolean isDuplicate(Log log, List<String> dupList, Station station, DataType type, long period, String measurementType, long timestamp, Object value) {
		String dupCheck = measurementType + timestamp + value.hashCode();
		if (dupList.contains(dupCheck)) {
			log.info(
				String.format(
					"Measurement duplicate of type %s found in list, skipping! (stationtype, stationcode, cname, period, timestamp, double_value) = (%s, %s, %s, %d, %d, %s) ",
					measurementType,
					station.getStationtype(),
					station.getStationcode(),
					type.getCname(),
					period,
					timestamp,
					value.toString()
				)
			);
			return true;
		}
		dupList.add(dupCheck);
		return false;
	}

    private static List<RecordDto> castToDtos(List<MeasurementAbstractHistory> result, boolean setPeriod) {
        List<RecordDto> dtos = new ArrayList<>();
        for (MeasurementAbstractHistory m : result) {
            SimpleRecordDto dto = new SimpleRecordDto(m.getTimestamp().getTime(), m.getValue(), setPeriod ? m.getPeriod() : null);
            dto.setCreated_on(m.getCreated_on().getTime());
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * <p>
     * the only method which requests history data from the biggest existing tables in the underlying DB,<br/>
     * it's very important that indexes are set correctly to avoid bad performance
     * </p>
     * @param em entity manager
     * @param typology of the specific station, e.g., MeteoStation, EnvironmentStation
     * @param identifier unique station identifier, required
     * @param cname unique type identifier, required
     * @param start time filter start in milliseconds UTC for query, required
     * @param end time filter start in milliseconds UTC for query, required
     * @param period interval between measurements
     * @param tableObject implementation which calls this method to decide which table to query, required
     * @return a list of measurements from history tables
     */
    protected static <T> List<RecordDto> findRecordsImpl(EntityManager em, String stationtype, String identifier, String cname, Date start, Date end, Integer period, T tableObject) {
        List<MeasurementAbstractHistory> result = QueryBuilder
                .init(em)
                .addSql("SELECT record")
                .addSql("FROM  " + tableObject.getClass().getSimpleName() + " record",
                        "WHERE record.station = (",
                        "SELECT s FROM Station s WHERE s.stationtype = :stationtype AND s.stationcode = :stationcode",
                        ")",
                        "AND record.type = (SELECT t FROM DataType t WHERE t.cname = :cname)",
                        "AND record.timestamp between :start AND :end")
                .setParameterIfNotNull("period", period, "AND record.period = :period")
                .setParameter("stationtype", stationtype)
                .setParameter("stationcode", identifier)
                .setParameter("cname", cname)
                .setParameter("start", start)
                .setParameter("end", end)
                .addSql("ORDER BY record.timestamp")
                .buildResultList(MeasurementAbstractHistory.class);
        return MeasurementAbstractHistory.castToDtos(result, period == null);
    }
}
