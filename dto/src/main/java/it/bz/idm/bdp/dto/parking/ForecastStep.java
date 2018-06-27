/**
 * dto - Data Transport Objects for an object-relational mapping
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
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
