package it.bz.idm.bdp.dal.bluetooth;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;

@Entity
@Table(name="bluetoothbasicdata",schema="intime")
public class BluetoothBasicData extends BasicData {
	
	private String sim_number;
	private String sim_serial;
	
	public String getSim_number() {
		return sim_number;
	}
	public void setSim_number(String sim_number) {
		this.sim_number = sim_number;
	}
	public String getSim_serial() {
		return sim_serial;
	}
	public void setSim_serial(String sim_serial) {
		this.sim_serial = sim_serial;
	}
	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		// TODO Auto-generated method stub
		return null;
	}

}
