/**
 * BDP data - Data Access Layer for the Big Data Platform
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
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
package it.bz.idm.bdp.dal.vehicle;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.vehicles.CarValue;

@Entity
@Table(name="measurementmobile",schema="intime")
public class TrafficVehicleRecord {

	public static final List<String[]> DATATYPES = new ArrayList<String[]>(){
	    /**
		 *
		 */
		private static final long serialVersionUID = 8282705506071191297L;

		{

	        add(new String[]{"o3_1_ppb","","",""});

	    	add(new String[]{"o3_1_runtime_s","","",""});

	    	add(new String[]{"o3_1_valid_b","","",""});

	    	add(new String[]{"no2_1_microgm3_ma","","",""});

	    	add(new String[]{"no2_1_microgm3_exp","","",""});

	        add(new String[]{"no2_1_ppb","","",""});

	        add(new String[]{"no2_1_runtime_s","","",""});

	    	add(new String[]{"no2_1_valid_b","","",""});

	    	add(new String[]{"no2_2_ppb","","",""});

	    	add(new String[]{"no2_2_runtime_s","","",""});

	    	add(new String[]{"no2_2_valid_b","","",""});

	    	add(new String[]{"co_1_ppm","","",""});

	    	add(new String[]{"co_1_runtime_s","","",""});

	    	add(new String[]{"co_1_valid_b","","",""});

	    	add(new String[]{"res_1_a","","",""});

	    	add(new String[]{"res_1_runtime_s","","",""});

	    	add(new String[]{"res_1_valid_b","","",""});

	    	add(new String[]{"res_2_a","","",""});

	    	add(new String[]{"res_2_runtime_s","","",""});

	    	add(new String[]{"res_2_valid_b","","",""});

	    	add(new String[]{"temp_1_c","","",""});

	    	add(new String[]{"temp_1_valid_b","","",""});

	    	add(new String[]{"rh_1_pct","","",""});

	    	add(new String[]{"rh_1_valid_b","","",""});

	    	add(new String[]{"af_1_sccm","","",""});

	    	add(new String[]{"af_1_valid_b","","",""});

	    	add(new String[]{"gps_1_long_deg","","",""});

	    	add(new String[]{"gps_1_lat_deg","","",""});

	    	add(new String[]{"gps_1_alt_m","","",""});

	    	add(new String[]{"gps_1_speed_mps","","",""});

	    	add(new String[]{"gps_1_hdg_deg","","",""});

	    	add(new String[]{"gps_1_sat_nr","","",""});

	    	add(new String[]{"gps_1_pdop_nr","","",""});

	    	add(new String[]{"gps_1_valid_b","","",""});

	    	add(new String[]{"id_vehicle_nr","","",""});

	    	add(new String[]{"id_system_nr","","",""});

	    	add(new String[]{"id_driver_nr","","",""});

	    	add(new String[]{"id_version_char","","",""});

	    	add(new String[]{"id_runtime_s","","",""});

	    	add(new String[]{"id_status_char","","",""});

	    	add(new String[]{"can_speed_mps","","",""});

	    	add(new String[]{"can_acc_long_mps2","","",""});

	    	add(new String[]{"can_acc_lat_mps2","","",""});

	    	add(new String[]{"can_acc_long_mean_mps2","","",""});

	    	add(new String[]{"can_acc_lat_mean_mps2","","",""});

	    	add(new String[]{"can_acc_long_var_m2ps4","","",""});

	    	add(new String[]{"can_acc_lat_var_m2ps4","","",""});

	    	add(new String[]{"can_valid_b","","",""});

	    	add(new String[]{"imu_speed_mps","","",""});

	    	add(new String[]{"imu_acc_long_mps2","","",""});

	    	add(new String[]{"imu_acc_lat_mps2","","",""});

	    	add(new String[]{"imu_acc_long_mean_mps2","","",""});

	    	add(new String[]{"imu_acc_lat_mean_mps2","","",""});

	    	add(new String[]{"imu_acc_long_var_m2ps4","","",""});

	    	add(new String[]{"imu_acc_lat_var_m2ps4","","",""});

	    	add(new String[]{"imu_valid_b","","",""});

	    	add(new String[]{"realtime_delay","","",""});
	    }
	};

	@Id
	@GeneratedValue(generator = "trafficvehiclerecord_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "trafficvehiclerecord_gen", sequenceName = "trafficvehiclerecord_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('trafficvehiclerecord_seq')")
	private Long id;

	//@Type(type="org.hibernate.spatial.JTSGeometryType")
	private Point position;

    private Double o3_1_ppb;

	private Integer o3_1_runtime_s;

	private Boolean o3_1_valid_b;

    private Double no2_1_ppb;

    private Integer no2_1_runtime_s;

	private Boolean no2_1_valid_b;

	private Double no2_2_ppb;

	private Integer no2_2_runtime_s;

	private Boolean no2_2_valid_b;

	private Double co_1_ppm;

	private Integer co_1_runtime_s;

	private Boolean co_1_valid_b;

	private Double res_1_a;

	private Integer res_1_runtime_s;

	private Boolean res_1_valid_b;

	private Double res_2_a;

	private Integer res_2_runtime_s;

	private Boolean res_2_valid_b;

	private Double temp_1_c;

	private Boolean temp_1_valid_b;

	private Double rh_1_pct;

	private Boolean rh_1_valid_b;

	private Double af_1_sccm;

	private Boolean af_1_valid_b;

	private Double gps_1_long_deg;

	private Double gps_1_lat_deg;

	private Double gps_1_alt_m;

	private Double gps_1_speed_mps;

	private Double gps_1_hdg_deg;

	private Integer gps_1_sat_nr;

	private Double gps_1_pdop_nr;

	private Boolean gps_1_valid_b;

	private Integer id_vehicle_nr;

	private Integer id_system_nr;

	private Integer id_driver_nr;

	private String id_version_char;

	private Integer id_runtime_s;

	private String id_status_char;

	private Double can_speed_mps;

	private Double can_acc_long_mps2;

	private Double can_acc_lat_mps2;

	private Double can_acc_long_mean_mps2;

	private Double can_acc_lat_mean_mps2;

	private Double can_acc_long_var_m2ps4;

	private Double can_acc_lat_var_m2ps4;

	private Boolean can_valid_b;

	private Double imu_speed_mps;

	private Double imu_acc_long_mps2;

	private Double imu_acc_lat_mps2;

	private Double imu_acc_long_mean_mps2;

	private Double imu_acc_lat_mean_mps2;

	private Double imu_acc_long_var_m2ps4;

	private Double imu_acc_lat_var_m2ps4;

	private Boolean imu_valid_b;

    @OneToOne(cascade=CascadeType.ALL)
    private Mobilestation station;

    @Temporal(TemporalType.TIMESTAMP)
    private Date ts_ms;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_on;

    private Long realtime_delay;

	private Double no2_1_microgm3_ma;

	private Double no2_1_microgm3_exp;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Double getO3_1_ppb() {
		return o3_1_ppb;
	}

	public void setO3_1_ppb(Double o3_1_ppb) {
		this.o3_1_ppb = o3_1_ppb;
	}

	public Integer getO3_1_runtime_s() {
		return o3_1_runtime_s;
	}

	public void setO3_1_runtime_s(Integer o3_1_runtime_s) {
		this.o3_1_runtime_s = o3_1_runtime_s;
	}

	public Boolean getO3_1_valid_b() {
		return o3_1_valid_b;
	}

	public void setO3_1_valid_b(Boolean o3_1_valid_b) {
		this.o3_1_valid_b = o3_1_valid_b;
	}

	public Double getNo2_1_ppb() {
		return no2_1_ppb;
	}

	public void setNo2_1_ppb(Double no2_1_ppb) {
		this.no2_1_ppb = no2_1_ppb;
	}

	public Integer getNo2_1_runtime_s() {
		return no2_1_runtime_s;
	}

	public void setNo2_1_runtime_s(Integer no2_1_runtime_s) {
		this.no2_1_runtime_s = no2_1_runtime_s;
	}

	public Boolean getNo2_1_valid_b() {
		return no2_1_valid_b;
	}

	public void setNo2_1_valid_b(Boolean no2_1_valid_b) {
		this.no2_1_valid_b = no2_1_valid_b;
	}

	public Double getNo2_2_ppb() {
		return no2_2_ppb;
	}

	public void setNo2_2_ppb(Double no2_2_ppb) {
		this.no2_2_ppb = no2_2_ppb;
	}

	public Integer getNo2_2_runtime_s() {
		return no2_2_runtime_s;
	}

	public void setNo2_2_runtime_s(Integer no2_2_runtime_s) {
		this.no2_2_runtime_s = no2_2_runtime_s;
	}

	public Boolean getNo2_2_valid_b() {
		return no2_2_valid_b;
	}

	public void setNo2_2_valid_b(Boolean no2_2_valid_b) {
		this.no2_2_valid_b = no2_2_valid_b;
	}

	public Double getCo_1_ppm() {
		return co_1_ppm;
	}

	public void setCo_1_ppm(Double co_1_ppm) {
		this.co_1_ppm = co_1_ppm;
	}

	public Integer getCo_1_runtime_s() {
		return co_1_runtime_s;
	}

	public void setCo_1_runtime_s(Integer co_1_runtime_s) {
		this.co_1_runtime_s = co_1_runtime_s;
	}

	public Boolean getCo_1_valid_b() {
		return co_1_valid_b;
	}

	public void setCo_1_valid_b(Boolean co_1_valid_b) {
		this.co_1_valid_b = co_1_valid_b;
	}

	public Double getRes_1_a() {
		return res_1_a;
	}

	public void setRes_1_a(Double res_1_a) {
		this.res_1_a = res_1_a;
	}

	public Integer getRes_1_runtime_s() {
		return res_1_runtime_s;
	}

	public void setRes_1_runtime_s(Integer res_1_runtime_s) {
		this.res_1_runtime_s = res_1_runtime_s;
	}

	public Boolean getRes_1_valid_b() {
		return res_1_valid_b;
	}

	public void setRes_1_valid_b(Boolean res_1_valid_b) {
		this.res_1_valid_b = res_1_valid_b;
	}

	public Double getRes_2_a() {
		return res_2_a;
	}

	public void setRes_2_a(Double res_2_a) {
		this.res_2_a = res_2_a;
	}

	public Integer getRes_2_runtime_s() {
		return res_2_runtime_s;
	}

	public void setRes_2_runtime_s(Integer res_2_runtime_s) {
		this.res_2_runtime_s = res_2_runtime_s;
	}

	public Boolean getRes_2_valid_b() {
		return res_2_valid_b;
	}

	public void setRes_2_valid_b(Boolean res_2_valid_b) {
		this.res_2_valid_b = res_2_valid_b;
	}

	public Double getTemp_1_c() {
		return temp_1_c;
	}

	public void setTemp_1_c(Double temp_1_c) {
		this.temp_1_c = temp_1_c;
	}

	public Boolean getTemp_1_valid_b() {
		return temp_1_valid_b;
	}

	public void setTemp_1_valid_b(Boolean temp_1_valid_b) {
		this.temp_1_valid_b = temp_1_valid_b;
	}

	public Double getRh_1_pct() {
		return rh_1_pct;
	}

	public void setRh_1_pct(Double rh_1_pct) {
		this.rh_1_pct = rh_1_pct;
	}

	public Boolean getRh_1_valid_b() {
		return rh_1_valid_b;
	}

	public void setRh_1_valid_b(Boolean rh_1_valid_b) {
		this.rh_1_valid_b = rh_1_valid_b;
	}

	public Double getAf_1_sccm() {
		return af_1_sccm;
	}

	public void setAf_1_sccm(Double af_1_sccm) {
		this.af_1_sccm = af_1_sccm;
	}

	public Boolean getAf_1_valid_b() {
		return af_1_valid_b;
	}

	public void setAf_1_valid_b(Boolean af_1_valid_b) {
		this.af_1_valid_b = af_1_valid_b;
	}

	public Double getGps_1_long_deg() {
		return gps_1_long_deg;
	}

	public void setGps_1_long_deg(Double gps_1_long_deg) {
		this.gps_1_long_deg = gps_1_long_deg;
	}

	public Double getGps_1_lat_deg() {
		return gps_1_lat_deg;
	}

	public void setGps_1_lat_deg(Double gps_1_lat_deg) {
		this.gps_1_lat_deg = gps_1_lat_deg;
	}

	public Double getGps_1_alt_m() {
		return gps_1_alt_m;
	}

	public void setGps_1_alt_m(Double gps_1_alt_m) {
		this.gps_1_alt_m = gps_1_alt_m;
	}

	public Double getGps_1_speed_mps() {
		return gps_1_speed_mps;
	}

	public void setGps_1_speed_mps(Double gps_1_speed_mps) {
		this.gps_1_speed_mps = gps_1_speed_mps;
	}

	public Double getGps_1_hdg_deg() {
		return gps_1_hdg_deg;
	}

	public void setGps_1_hdg_deg(Double gps_1_hdg_deg) {
		this.gps_1_hdg_deg = gps_1_hdg_deg;
	}

	public Integer getGps_1_sat_nr() {
		return gps_1_sat_nr;
	}

	public void setGps_1_sat_nr(Integer gps_1_sat_nr) {
		this.gps_1_sat_nr = gps_1_sat_nr;
	}

	public Double getGps_1_pdop_nr() {
		return gps_1_pdop_nr;
	}

	public void setGps_1_pdop_nr(Double gps_1_pdop_nr) {
		this.gps_1_pdop_nr = gps_1_pdop_nr;
	}

	public Boolean getGps_1_valid_b() {
		return gps_1_valid_b;
	}

	public void setGps_1_valid_b(Boolean gps_1_valid_b) {
		this.gps_1_valid_b = gps_1_valid_b;
	}

	public Integer getId_vehicle_nr() {
		return id_vehicle_nr;
	}

	public void setId_vehicle_nr(Integer id_vehicle_nr) {
		this.id_vehicle_nr = id_vehicle_nr;
	}

	public Integer getId_system_nr() {
		return id_system_nr;
	}

	public void setId_system_nr(Integer id_system_nr) {
		this.id_system_nr = id_system_nr;
	}

	public Integer getId_driver_nr() {
		return id_driver_nr;
	}

	public void setId_driver_nr(Integer id_driver_nr) {
		this.id_driver_nr = id_driver_nr;
	}

	public String getId_version_char() {
		return id_version_char;
	}

	public void setId_version_char(String id_version_char) {
		this.id_version_char = id_version_char;
	}

	public Integer getId_runtime_s() {
		return id_runtime_s;
	}

	public void setId_runtime_s(Integer id_runtime_s) {
		this.id_runtime_s = id_runtime_s;
	}

	public String getId_status_char() {
		return id_status_char;
	}

	public void setId_status_char(String id_status_char) {
		this.id_status_char = id_status_char;
	}

	public Double getCan_speed_mps() {
		return can_speed_mps;
	}

	public void setCan_speed_mps(Double can_speed_mps) {
		this.can_speed_mps = can_speed_mps;
	}

	public Double getCan_acc_long_mps2() {
		return can_acc_long_mps2;
	}

	public void setCan_acc_long_mps2(Double can_acc_long_mps2) {
		this.can_acc_long_mps2 = can_acc_long_mps2;
	}

	public Double getCan_acc_lat_mps2() {
		return can_acc_lat_mps2;
	}

	public void setCan_acc_lat_mps2(Double can_acc_lat_mps2) {
		this.can_acc_lat_mps2 = can_acc_lat_mps2;
	}

	public Double getCan_acc_long_mean_mps2() {
		return can_acc_long_mean_mps2;
	}

	public void setCan_acc_long_mean_mps2(Double can_acc_long_mean_mps2) {
		this.can_acc_long_mean_mps2 = can_acc_long_mean_mps2;
	}

	public Double getCan_acc_lat_mean_mps2() {
		return can_acc_lat_mean_mps2;
	}

	public void setCan_acc_lat_mean_mps2(Double can_acc_lat_mean_mps2) {
		this.can_acc_lat_mean_mps2 = can_acc_lat_mean_mps2;
	}

	public Double getCan_acc_long_var_m2ps4() {
		return can_acc_long_var_m2ps4;
	}

	public void setCan_acc_long_var_m2ps4(Double can_acc_long_var_m2ps4) {
		this.can_acc_long_var_m2ps4 = can_acc_long_var_m2ps4;
	}

	public Double getCan_acc_lat_var_m2ps4() {
		return can_acc_lat_var_m2ps4;
	}

	public void setCan_acc_lat_var_m2ps4(Double can_acc_lat_var_m2ps4) {
		this.can_acc_lat_var_m2ps4 = can_acc_lat_var_m2ps4;
	}

	public Boolean getCan_valid_b() {
		return can_valid_b;
	}

	public void setCan_valid_b(Boolean can_valid_b) {
		this.can_valid_b = can_valid_b;
	}

	public Double getImu_speed_mps() {
		return imu_speed_mps;
	}

	public void setImu_speed_mps(Double imu_speed_mps) {
		this.imu_speed_mps = imu_speed_mps;
	}

	public Double getImu_acc_long_mps2() {
		return imu_acc_long_mps2;
	}

	public void setImu_acc_long_mps2(Double imu_acc_long_mps2) {
		this.imu_acc_long_mps2 = imu_acc_long_mps2;
	}

	public Double getImu_acc_lat_mps2() {
		return imu_acc_lat_mps2;
	}

	public void setImu_acc_lat_mps2(Double imu_acc_lat_mps2) {
		this.imu_acc_lat_mps2 = imu_acc_lat_mps2;
	}

	public Double getImu_acc_long_mean_mps2() {
		return imu_acc_long_mean_mps2;
	}

	public void setImu_acc_long_mean_mps2(Double imu_acc_long_mean_mps2) {
		this.imu_acc_long_mean_mps2 = imu_acc_long_mean_mps2;
	}

	public Double getImu_acc_lat_mean_mps2() {
		return imu_acc_lat_mean_mps2;
	}

	public void setImu_acc_lat_mean_mps2(Double imu_acc_lat_mean_mps2) {
		this.imu_acc_lat_mean_mps2 = imu_acc_lat_mean_mps2;
	}

	public Double getImu_acc_long_var_m2ps4() {
		return imu_acc_long_var_m2ps4;
	}

	public void setImu_acc_long_var_m2ps4(Double imu_acc_long_var_m2ps4) {
		this.imu_acc_long_var_m2ps4 = imu_acc_long_var_m2ps4;
	}

	public Double getImu_acc_lat_var_m2ps4() {
		return imu_acc_lat_var_m2ps4;
	}

	public void setImu_acc_lat_var_m2ps4(Double imu_acc_lat_var_m2ps4) {
		this.imu_acc_lat_var_m2ps4 = imu_acc_lat_var_m2ps4;
	}

	public Boolean getImu_valid_b() {
		return imu_valid_b;
	}

	public void setImu_valid_b(Boolean imu_valid_b) {
		this.imu_valid_b = imu_valid_b;
	}

	public Mobilestation getStation() {
		return station;
	}

	public void setStation(Mobilestation station) {
		this.station = station;
	}

	public Date getTs_ms() {
		return ts_ms;
	}

	public void setTs_ms(Date ts_ms) {
		this.ts_ms = ts_ms;
	}
    public Long getRealtime_delay() {
		return realtime_delay;
	}

	public void setRealtime_delay(Long realtime_delay) {
		this.realtime_delay = realtime_delay;
	}


	public Date getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}
	public Double getNo2_1_microgm3_ma() {
		return no2_1_microgm3_ma;
	}

	public void setNo2_1_microgm3_ma(Double no2_1_microgm3_ma) {
		this.no2_1_microgm3_ma = no2_1_microgm3_ma;
	}


	public Double getNo2_1_microgm3_exp() {
		return no2_1_microgm3_exp;
	}

	public void setNo2_1_microgm3_exp(Double no2_1_microgm3_exp) {
		this.no2_1_microgm3_exp = no2_1_microgm3_exp;
	}

	public TrafficVehicleRecord() {
	}

	public TrafficVehicleRecord(CarValue value, Mobilestation station) {
		super();
    	this.o3_1_ppb = value.getO3_1_ppb();
    	this.o3_1_runtime_s = value.getO3_1_runtime_s();
    	this.o3_1_valid_b = value.getO3_1_valid_b();
		this.no2_1_ppb = value.getNo2_1_ppb();
		this.no2_1_runtime_s = value.getNo2_1_runtime_s();
		this.no2_1_valid_b = value.getNo2_1_valid_b();
		this.no2_2_ppb = value.getNo2_2_ppb();
		this.no2_2_runtime_s = value.getNo2_2_runtime_s();
		this.no2_2_valid_b = value.getNo2_2_valid_b();
		this.co_1_ppm = value.getCo_1_ppm();
		this.co_1_runtime_s = value.getCo_1_runtime_s();
		this.co_1_valid_b = value.getCo_1_valid_b();
		this.res_1_a = value.getRes_1_a();
		this.res_1_runtime_s = value.getRes_1_runtime_s();
		this.res_1_valid_b = value.getRes_1_valid_b();
		this.res_2_a = value.getRes_2_a();
		this.res_2_runtime_s = value.getRes_2_runtime_s();
		this.res_2_valid_b = value.getRes_2_valid_b();
		this.temp_1_c = value.getTemp_1_c();
		this.temp_1_valid_b = value.getTemp_1_valid_b();
		this.rh_1_pct = value.getRh_1_pct();
		this.rh_1_valid_b = value.getRh_1_valid_b();
		this.af_1_sccm = value.getAf_1_sccm();
		this.af_1_valid_b = value.getAf_1_valid_b();
		this.gps_1_long_deg = value.getGps_1_long_deg();
		this.gps_1_lat_deg = value.getGps_1_lat_deg();
		this.gps_1_alt_m = value.getGps_1_alt_m();
		this.gps_1_speed_mps = value.getGps_1_speed_mps();
		this.gps_1_hdg_deg = value.getGps_1_hdg_deg();
		this.gps_1_sat_nr = value.getGps_1_sat_nr();
		this.gps_1_pdop_nr = value.getGps_1_pdop_nr();
		this.gps_1_valid_b = value.getGps_1_valid_b();
		this.id_vehicle_nr = value.getId_vehicle_nr();
		this.id_system_nr = value.getId_system_nr();
		this.id_driver_nr = value.getId_driver_nr();
		this.id_version_char = value.getId_version_char();
		this.id_runtime_s = value.getId_runtime_s();
		this.id_status_char = value.getId_status_char();
		this.can_speed_mps = value.getCan_speed_mps();
		this.can_acc_long_mps2 = value.getCan_acc_long_mps2();
		this.can_acc_lat_mps2 = value.getCan_acc_lat_mps2();
		this.can_acc_long_mean_mps2 = value.getCan_acc_long_mean_mps2();
		this.can_acc_lat_mean_mps2 = value.getCan_acc_lat_mean_mps2();
		this.can_acc_long_var_m2ps4 = value.getCan_acc_long_var_m2ps4();
		this.can_acc_lat_var_m2ps4 = value.getCan_acc_lat_var_m2ps4();
		this.can_valid_b = value.getCan_valid_b();
		this.imu_speed_mps = value.getImu_speed_mps();
		this.imu_acc_long_mps2 = value.getImu_acc_long_mps2();
		this.imu_acc_lat_mps2 = value.getImu_acc_lat_mps2();
		this.imu_acc_long_mean_mps2 = value.getImu_acc_long_mean_mps2();
		this.imu_acc_lat_mean_mps2 = value.getImu_acc_lat_mean_mps2();
		this.imu_acc_long_var_m2ps4 = value.getImu_acc_long_var_m2ps4();
		this.imu_acc_lat_var_m2ps4 = value.getImu_acc_lat_var_m2ps4();
		this.imu_valid_b = value.getImu_valid_b();
		this.station = station;
    	this.ts_ms = new Date(value.getTs_ms());
    	this.created_on = new Date();
    	this.realtime_delay = Math.round((created_on.getTime()-ts_ms.getTime())/1000.);
    	if ((gps_1_long_deg != null && gps_1_long_deg != 0) && (gps_1_lat_deg!=null && gps_1_lat_deg != 0)){
    		this.position = Station.geometryFactory.createPoint(new Coordinate(gps_1_long_deg, gps_1_lat_deg));
    	}

	}
	public boolean alreadyExists() {
		List<TrafficVehicleRecord> resultList = findTrafficVehicleRecordsByVehicleAndTs_msEquals(station, ts_ms);
		return !resultList.isEmpty();

	}

	private List<TrafficVehicleRecord> findTrafficVehicleRecordsByVehicleAndTs_msEquals(
			Mobilestation station, Date ts_ms) {
		EntityManager em = JPAUtil.createEntityManager();
		TypedQuery<TrafficVehicleRecord> query = em.createQuery("select record from TrafficVehicleRecord record where record.station= :station AND record.ts_ms= :ts_ms",TrafficVehicleRecord.class);
		query.setParameter("station", station);
		query.setParameter("ts_ms", ts_ms);
		List<TrafficVehicleRecord> resultList = query.getResultList();
		em.close();
		return resultList;
	}

	public static Date findRecordTimestampByVehicle(String stationId) {
		EntityManager em = JPAUtil.createEntityManager();
		TypedQuery<Date> query = em.createQuery(
				"select record.ts_ms from TrafficVehicleRecord record where record.station.stationcode= :station ORDER BY record.ts_ms desc",
				Date.class);
		query.setParameter("station", stationId);
		Date result = JPAUtil.getSingleResultOrNull(query);
		em.close();
		return result;
	}

	public static TrafficVehicleRecord findRecordByVehicle(EntityManager em, Mobilestation trafficVehicle){
		TypedQuery<TrafficVehicleRecord > query = em.createQuery("select record from TrafficVehicleRecord record where record.station= :station",TrafficVehicleRecord.class);
		query.setParameter("station", trafficVehicle);
		return JPAUtil.getSingleResultOrNull(query);
	}
	public static TrafficVehicleRecord findRecordByVehicle(EntityManager em, Mobilestation trafficVehicle, String cname, Integer period){
		TrafficVehicleRecord vehicleRecord = null;
		if (trafficVehicle != null ){
			if (period==null && cname != null)
				vehicleRecord = findRecordByVehicle(em, trafficVehicle, cname);
			else
				vehicleRecord = findRecordByVehicle(em, trafficVehicle);
		}
		return vehicleRecord;
	}

	private static TrafficVehicleRecord findRecordByVehicle(EntityManager em,
			Mobilestation trafficVehicle, String type) {
		TypedQuery<TrafficVehicleRecord> query = em.createQuery(
				"select record from TrafficVehicleRecord record where record.station= :station and :type is not null",
				TrafficVehicleRecord.class);
		query.setParameter("station", trafficVehicle);
		query.setParameter("type", type);
		return JPAUtil.getSingleResultOrNull(query);
	}

}
