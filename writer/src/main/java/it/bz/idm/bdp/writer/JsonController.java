package it.bz.idm.bdp.writer;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.StationDto;

@RequestMapping("/json")
@Controller
public class JsonController extends DataManager{

	@RequestMapping(value = "/getDateOfLastRecord/{stationtype}/{stationCode}/{dataType}/{period}", method = RequestMethod.GET)
	public @ResponseBody Date getDateOfLastRecord(@PathVariable("stationType") String stationType,@PathVariable("stationCode") String stationCode,
			@PathVariable("dataType") String dataType, @PathVariable("period") Integer period) {
		return (Date) super.getDateOfLastRecord(stationType, stationCode, dataType, period);
	}

	@RequestMapping(value = "/pushRecords/{integreenTypology}", method = RequestMethod.POST)
	public @ResponseBody Object pushRecords(@RequestBody(required = true) DataMapDto stationData,
			@PathVariable String integreenTypology) {
		return super.pushRecords(integreenTypology, stationData);
	}

	@RequestMapping(value = "/syncStations/{integreenTypology}", method = RequestMethod.POST)
	public @ResponseBody Object syncStations(@RequestBody(required = true) List<StationDto> data,
			@PathVariable String integreenTypology) {
		return super.syncStations(integreenTypology, data);
	}

	@RequestMapping(value = "/syncDataTypes", method = RequestMethod.POST)
	public Object syncDataTypes(Object... data) {
		return super.syncDataTypes(null, data);
	}
}