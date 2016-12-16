package it.bz.idm.bdp.dal;

import it.bz.idm.bdp.dal.meteo.Meteostation;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


@Entity
public class Trafficstation extends ElaborationStation{

	public List<String[]> findDataTypes(EntityManager em, String stationId) {
		TypedQuery<Object[]> query;
		if (stationId == null) {
			query = em.createQuery("SELECT elab.type.cname,elab.type.cunit,elab.type.description,elab.period FROM Elaboration elab INNER JOIN elab.type where elab.station.class=:stationType GROUP BY elab.type.cname,elab.type.cunit,elab.type.description,elab.period",Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
		} else {
			query = em.createQuery("SELECT elab.type.cname,elab.type.cunit,elab.type.description,elab.period FROM Elaboration elab INNER JOIN elab.type where elab.station.class=:stationType AND elab.station.stationcode=:station GROUP BY elab.type.cname,elab.type.cunit,elab.type.description,elab.period",Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
			query.setParameter("station",stationId);
		}

		List<Object[]> resultList = query.getResultList();
		if (resultList.isEmpty())
			return new Meteostation().findDataTypes(em,stationId);
		return getDataTypesFromQuery(resultList);
	}

	@Override
	public RecordDto findLastRecord(EntityManager em,String cname, Integer period) {
		RecordDto record = super.findLastRecord(em,cname,period);
		if (record == null){
			DataType type = DataType.findByCname(em, cname);
			Measurement measurement = Measurement.findLatestEntry(em, this, type, period);
			record = new SimpleRecordDto(measurement.getTimestamp().getTime(),measurement.getValue(), period);
		}
		return record;
	}

	@Override
	public List<RecordDto> getRecords(EntityManager em, String type, Date start, Date end,
			Integer period) {
		List<RecordDto> records = null;
		records = ElaborationHistory.findRecords(em,this.getClass().getSimpleName(),this.stationcode,type,start,end,period);
		if (records.isEmpty())
			records = MeasurementHistory.findRecords(em,this.getClass().getSimpleName(), this.stationcode, type, start, end, period);
		return records;
	}

	@Override
	public Object pushRecords(EntityManager em, Object... object) {
		return null;
	}

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}

}
