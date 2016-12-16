package it.bz.idm.bdp.dto.parking;

import java.sql.Timestamp;
import java.util.ArrayList;


public class ParkingForecasts {
	private static final int PERIODICITY_IN_MINUTES = 30;
	private Timestamp timestamp= null;
	private ArrayList<ParkingForecast> parkingForecasts = new ArrayList<ParkingForecast>();
	
	/**
	 * @return the timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the parkingForecasts
	 */
	public ArrayList<ParkingForecast> getParkingForecasts() {
		return parkingForecasts;
	}
	/**
	 * @param parkingForecasts the parkingForecasts to set
	 */
	public void setParkingForecasts(ArrayList<ParkingForecast> parkingForecasts) {
		this.parkingForecasts = parkingForecasts;
	}
	public void addOneParkingforecast(ParkingForecast f){
		this.parkingForecasts.add(f);
	}
	public Object getParkingPredictionByPosition(
			int[] predictionForecastTimesInMinutes) {
		// TODO Auto-generated method stub
		return null;
	}
	public ParkingForecast findByTime(Integer period) {
		int position = (period/PERIODICITY_IN_MINUTES) -1;
		if (position < parkingForecasts.size())
			return parkingForecasts.get(position);
		return null;
	}
}
