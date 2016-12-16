package it.bz.idm.bdp.dto;

import it.bz.idm.bdp.dto.RecordDtoImpl;

public class TrafficVehicleRecordDto extends RecordDtoImpl{
	    /**
	 * 
	 */
	private static final long serialVersionUID = -32256053056092683L;


		
		private String value;
		
		public TrafficVehicleRecordDto() {
		}

		public TrafficVehicleRecordDto(Long timestamp, String value) {
			super();
			this.timestamp = timestamp;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
	    
}
