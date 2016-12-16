package it.bz.idm.bdp.dto.parking;

import java.sql.Timestamp;
import java.util.ArrayList;


public class ForecastStep {
	private Timestamp forecastStartDate;
	private Timestamp forecastEndDate;
	private Weather weather= null;
	private Holiday holiday = null;
	private Event event = null;
	private ArrayList<ParkingPrediction> parkingPredictions = null;
	
	
	public void setForecastStartDate(Timestamp timestamp) {
		this.forecastStartDate = timestamp;
	}
	public Timestamp getForecastStartDate() {
		return forecastStartDate;
	}
	
	public Timestamp getForecastEndDate() {
		return forecastEndDate;
	}

	public void setForecastEndDate(Timestamp forecastEndDate) {
		this.forecastEndDate = forecastEndDate;
	}

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}

	public Holiday getHoliday() {
		return holiday;
	}

	public void setHoliday(Holiday holiday) {
		this.holiday = holiday;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public ArrayList<ParkingPrediction> getParkingPredictions() {
		return parkingPredictions;
	}

	public void setParkingPredictions(
			ArrayList<ParkingPrediction> parkingPredictions) {
		this.parkingPredictions = parkingPredictions;
	}
	public Integer getParkingPredictionByParkingId(Integer identifier) {
		for (ParkingPrediction prediction: parkingPredictions){
			if (identifier.equals(prediction.getParkingPlace().getParkingId()))
				return prediction.getPrediction().getPredictedFreeSlots();
		}
		return null;
	}
}
