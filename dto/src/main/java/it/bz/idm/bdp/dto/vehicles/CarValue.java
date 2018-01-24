package it.bz.idm.bdp.dto.vehicles;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CarValue implements Serializable{
	
	private Long ts_ms;

    private Double gps_lon;

    private Double gps_lat;

    private Double o3;
    
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
	
	private Double no2_1_µgm3_ma;

	@JsonCreator
	public CarValue(@JsonProperty("ts_ms") Long ts_ms,
			@JsonProperty("gps_lon") Double gps_lon, 
			@JsonProperty("gps_lat") Double gps_lat,
			@JsonProperty("O3") Double o3, 
			@JsonProperty("o3_1_ppb") Double o3_1_ppb,
			@JsonProperty("o3_1_runtime_s") Integer o3_1_runtime_s,
			@JsonProperty("o3_1_valid_b") Integer o3_1_valid_b,
			@JsonProperty("no2_1_ppb") Double no2_1_ppb,
			@JsonProperty("no2_1_runtime_s") Integer no2_1_runtime_s,
			@JsonProperty("no2_1_valid_b") Integer no2_1_valid_b,
			@JsonProperty("no2_2_ppb") Double no2_2_ppb,
			@JsonProperty("no2_2_runtime_s") Integer no2_2_runtime_s,
			@JsonProperty("no2_2_valid_b") Integer no2_2_valid_b,
			@JsonProperty("co_1_ppm") Double co_1_ppm, 
			@JsonProperty("co_1_runtime_s") Integer co_1_runtime_s,
			@JsonProperty("co_1_valid_b") Integer co_1_valid_b,
			@JsonProperty("res_1_a") Double res_1_a,
			@JsonProperty("res_1_runtime_s") Integer res_1_runtime_s,
			@JsonProperty("res_1_valid_b") Integer res_1_valid_b,
			@JsonProperty("res_2_a") Double res_2_a, 
			@JsonProperty("res_2_runtime_s") Integer res_2_runtime_s,
			@JsonProperty("res_2_valid_b") Integer res_2_valid_b,
			@JsonProperty("temp_1_c") Double temp_1_c,
			@JsonProperty("temp_1_valid_b") Integer temp_1_valid_b, 
			@JsonProperty("rh_1_pct") Double rh_1_pct,
			@JsonProperty("rh_1_valid_b") Integer rh_1_valid_b, 
			@JsonProperty("Double af_1_sccm") Double af_1_sccm,
			@JsonProperty("af_1_valid_b") Integer af_1_valid_b,
			@JsonProperty("gps_1_long_deg") Double gps_1_long_deg,
			@JsonProperty("gps_1_lat_deg") Double gps_1_lat_deg, 
			@JsonProperty("gps_1_alt_m") Double gps_1_alt_m,
			@JsonProperty("gps_1_speed_mps") Double gps_1_speed_mps, 
			@JsonProperty("gps_1_hdg_deg") Double gps_1_hdg_deg,
			@JsonProperty("gps_1_sat_nr") Integer gps_1_sat_nr,
			@JsonProperty("gps_1_pdop_nr") Double gps_1_pdop_nr,
			@JsonProperty("gps_1_valid_b") Integer gps_1_valid_b,
			@JsonProperty("id_vehicle_nr")Integer id_vehicle_nr,
			@JsonProperty("id_system_nr") Integer id_system_nr,
			@JsonProperty("id_driver_nr") Integer id_driver_nr,
			@JsonProperty("id_version_char") String id_version_char, 
			@JsonProperty("id_runtime_s")Integer id_runtime_s,
			@JsonProperty("id_status_char") String id_status_char, 
			@JsonProperty("can_speed_mps") Double can_speed_mps,
			@JsonProperty("can_acc_long_mps2") Double can_acc_long_mps2,
			@JsonProperty("can_acc_lat_mps2") Double can_acc_lat_mps2,
			@JsonProperty("can_acc_long_mean_mps2") Double can_acc_long_mean_mps2,
			@JsonProperty("can_acc_lat_mean_mps2") Double can_acc_lat_mean_mps2,
			@JsonProperty("can_acc_long_var_m2ps4") Double can_acc_long_var_m2ps4,
			@JsonProperty("can_acc_lat_var_m2ps4") Double can_acc_lat_var_m2ps4,
			@JsonProperty("can_valid_b") Integer can_valid_b, 
			@JsonProperty("imu_speed_mps") Double imu_speed_mps,
			@JsonProperty("imu_acc_long_mps2") Double imu_acc_long_mps2,
			@JsonProperty("imu_acc_lat_mps2") Double imu_acc_lat_mps2,
			@JsonProperty("imu_acc_long_mean_mps2") Double imu_acc_long_mean_mps2,
			@JsonProperty("imu_acc_lat_mean_mps2") Double imu_acc_lat_mean_mps2,
			@JsonProperty("imu_acc_long_var_m2ps4") Double imu_acc_long_var_m2ps4,
			@JsonProperty("imu_acc_lat_var_m2ps4") Double imu_acc_lat_var_m2ps4,
			@JsonProperty("imu_valid_b") Integer imu_valid_b, 
			@JsonProperty("no2_1_µgm3_ma") Double no2_1_µgm3_ma) {
		super();
		this.ts_ms = ts_ms;
		this.gps_lon = gps_lon;
		this.gps_lat = gps_lat;
		this.o3 = o3;
		this.o3_1_ppb = o3_1_ppb;
		this.o3_1_runtime_s = o3_1_runtime_s;
		this.o3_1_valid_b = castToBoolean(o3_1_valid_b);
		this.no2_1_ppb = no2_1_ppb;
		this.no2_1_runtime_s = no2_1_runtime_s;
		this.no2_1_valid_b = castToBoolean(no2_1_valid_b);
		this.no2_2_ppb = no2_2_ppb;
		this.no2_2_runtime_s = no2_2_runtime_s;
		this.no2_2_valid_b= castToBoolean( no2_2_valid_b);
		this.co_1_ppm = co_1_ppm;
		this.co_1_runtime_s = co_1_runtime_s;
		this.co_1_valid_b= castToBoolean( co_1_valid_b);
		this.res_1_a = res_1_a;
		this.res_1_runtime_s = res_1_runtime_s;
		this.res_1_valid_b= castToBoolean( res_1_valid_b);
		this.res_2_a = res_2_a;
		this.res_2_runtime_s = res_2_runtime_s;
		this.res_2_valid_b= castToBoolean( res_2_valid_b);
		this.temp_1_c = temp_1_c;
		this.temp_1_valid_b= castToBoolean( temp_1_valid_b);
		this.rh_1_pct = rh_1_pct;
		this.rh_1_valid_b= castToBoolean( rh_1_valid_b);
		this.af_1_sccm = af_1_sccm;
		this.af_1_valid_b= castToBoolean( af_1_valid_b);
		this.gps_1_long_deg = gps_1_long_deg;
		this.gps_1_lat_deg = gps_1_lat_deg;
		this.gps_1_alt_m = gps_1_alt_m;
		this.gps_1_speed_mps = gps_1_speed_mps;
		this.gps_1_hdg_deg = gps_1_hdg_deg;
		this.gps_1_sat_nr = gps_1_sat_nr;
		this.gps_1_pdop_nr= gps_1_pdop_nr;
		this.gps_1_valid_b = castToBoolean(gps_1_valid_b);
		this.id_vehicle_nr = id_vehicle_nr;
		this.id_system_nr = id_system_nr;
		this.id_driver_nr = id_driver_nr;
		this.id_version_char = id_version_char;
		this.id_runtime_s = id_runtime_s;
		this.id_status_char = id_status_char;
		this.can_speed_mps = can_speed_mps;
		this.can_acc_long_mps2 = can_acc_long_mps2;
		this.can_acc_lat_mps2 = can_acc_lat_mps2;
		this.can_acc_long_mean_mps2 = can_acc_long_mean_mps2;
		this.can_acc_lat_mean_mps2 = can_acc_lat_mean_mps2;
		this.can_acc_long_var_m2ps4 = can_acc_long_var_m2ps4;
		this.can_acc_lat_var_m2ps4 = can_acc_lat_var_m2ps4;
		this.can_valid_b= castToBoolean( can_valid_b);
		this.imu_speed_mps = imu_speed_mps;
		this.imu_acc_long_mps2 = imu_acc_long_mps2;
		this.imu_acc_lat_mps2 = imu_acc_lat_mps2;
		this.imu_acc_long_mean_mps2 = imu_acc_long_mean_mps2;
		this.imu_acc_lat_mean_mps2 = imu_acc_lat_mean_mps2;
		this.imu_acc_long_var_m2ps4 = imu_acc_long_var_m2ps4;
		this.imu_acc_lat_var_m2ps4 = imu_acc_lat_var_m2ps4;
		this.no2_1_µgm3_ma = no2_1_µgm3_ma;
		this.imu_valid_b= castToBoolean( imu_valid_b);
	}

	
    private Boolean castToBoolean(Integer value) {
		return (value != null)? value ==1:null;
	}


	public CarValue() {
    }


	public Long getTs_ms() {
		return ts_ms;
	}


	public void setTs_ms(Long ts_ms) {
		this.ts_ms = ts_ms;
	}


	public Double getGps_lon() {
		return gps_lon;
	}


	public void setGps_lon(Double gps_lon) {
		this.gps_lon = gps_lon;
	}


	public Double getGps_lat() {
		return gps_lat;
	}


	public void setGps_lat(Double gps_lat) {
		this.gps_lat = gps_lat;
	}


	public Double getO3() {
		return o3;
	}


	public void setO3(Double o3) {
		this.o3 = o3;
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


	public Double getNo2_1_µgm3_ma() {
		return no2_1_µgm3_ma;
	}


	public void setNo2_1_µgm3_ma(Double no2_1_µgm3_ma) {
		this.no2_1_µgm3_ma = no2_1_µgm3_ma;
	}

	
}
