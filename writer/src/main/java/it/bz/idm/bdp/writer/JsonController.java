package it.bz.idm.bdp.writer;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;

@RequestMapping("/json")
@Controller
public class JsonController extends DataManager{
	@RequestMapping(value = "/getDateOfLastRecord/{integreenTypology}/", method = RequestMethod.GET)
	public @ResponseBody Date getDateOfLastRecord(@PathVariable("integreenTypology") String stationType,@RequestParam("stationId") String stationId,
			@RequestParam(value="typeId") String typeId, @RequestParam(value="period",required=false) Integer period) {
		return (Date) super.getDateOfLastRecord(stationType, stationId, typeId, period);
	}
	
	@RequestMapping(value = "/stationsWithoutMunicipality", method = RequestMethod.GET)
	public @ResponseBody List<StationDto> getStationsWithoutMunicipality() {
		return super.getStationsWithoutMunicipality();
	}
	
	@RequestMapping(value = "/pushRecords/{integreenTypology}", method = RequestMethod.POST)
	public @ResponseBody Object pushRecords(@RequestBody(required = true) DataMapDto<RecordDtoImpl> stationData,
			@PathVariable String integreenTypology) {
		return super.pushRecords(integreenTypology, stationData);
	}
	@RequestMapping(value = "/patchStations", method = RequestMethod.POST)
	public @ResponseBody void patchStations(@RequestBody(required = true) List<StationDto> stations) {
		super.patchStations(stations);
	}
	@RequestMapping(value = "/syncStations/{integreenTypology}", method = RequestMethod.POST)
	public @ResponseBody Object syncStations(@RequestBody(required = true) List<StationDto> data,
			@PathVariable String integreenTypology) {
		return super.syncStations(integreenTypology, data);
	}

	@RequestMapping(value = "/syncDataTypes", method = RequestMethod.POST)
	public @ResponseBody Object syncDataTypes(@RequestBody(required = true) List<DataTypeDto> data) {
		return super.syncDataTypes(null, data);
	}
}