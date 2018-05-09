package it.bz.idm.bdp.reader;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;

@Controller
public class JsonController extends DataRetriever{

	@RequestMapping(value = "/stations", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<String> getStations(@RequestParam String stationType) {
		return super.getStations(stationType);
	}
	@RequestMapping(value = "/link-stations", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<StationDto> getAvailableStations() {
		return super.getAvailableStations();
	}
	@RequestMapping(value = "/child-stations", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<ChildDto> getChildren(@RequestParam String stationType,@RequestParam String parent) {
		return super.getChildren(stationType, parent);
	}
	@RequestMapping(value = "/data-types", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<String[]> getDataTypes(@RequestParam String stationType, @RequestParam(required=false) String stationId) {
		return super.getDataTypes(stationType, stationId);
	}
	@RequestMapping(value = "/types", method = RequestMethod.GET)
	@Override
	public @ResponseBody List<TypeDto> getTypes(String stationType, String stationId) {
		return super.getTypes(stationType, stationId);
	}
	@RequestMapping(value = "/date-of-last-record", method = RequestMethod.GET)
	@Override
	public @ResponseBody Date getDateOfLastRecord(@RequestParam String stationType,@RequestParam String stationId, @RequestParam(required=false) String typeId, @RequestParam(required=false) Integer period) {
		return super.getDateOfLastRecord(stationType, stationId, typeId, period);
	}
	@RequestMapping(value = "/last-record", method = RequestMethod.GET)
	@Override
	public @ResponseBody RecordDto getLastRecord(@RequestParam String stationType, @RequestParam String stationId, @RequestParam(required=false)String typeId, @RequestParam(required=false)Integer period) {
		return super.getLastRecord(stationType, stationId, typeId, period);
	}
	@RequestMapping(value = "/newest-record", method = RequestMethod.GET)
	@Override
	public @ResponseBody RecordDto getNewestRecord(@RequestParam String stationType,@RequestParam String stationId, @RequestParam(required=false) String typeId, @RequestParam(required=false) Integer period) {
		return super.getNewestRecord(stationType, stationId, typeId, period);
	}
	@RequestMapping(value = "/records", method = RequestMethod.GET)
	public @ResponseBody List<RecordDto> getRecords(@RequestParam String stationType,@RequestParam String stationId,@RequestParam(required=false) String typeId,@RequestParam(required=false) Long start,@RequestParam(required=false) Long end,
			@RequestParam(required=false) Integer period, @RequestParam(required=false)Integer seconds) {
		return super.getRecords(stationType, stationId, typeId, start != null ? new Date(start) : null,
				end != null ? new Date(end) : null, period, seconds);
	}
	@RequestMapping(value = "/station-details", method = RequestMethod.GET)
	@Override
	public @ResponseBody  List<? extends StationDto> getStationDetails(@RequestParam String stationType,@RequestParam(required=false) String stationId) {
		List<? extends StationDto> stationDetails = super.getStationDetails(stationType, stationId);
		return stationDetails;
	}
}
