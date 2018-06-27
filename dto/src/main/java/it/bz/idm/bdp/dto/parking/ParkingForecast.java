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


public class ParkingForecast{
	private Timestamp startDate;
	private Timestamp endDate;
	private ParkingPlace parkingPlace;
	private TSPrediction prediction;
	
	public ParkingForecast(ParkingPlace parkingPlace, TSPrediction prediction,
			Timestamp startdate, Timestamp enddate){
		this.parkingPlace = parkingPlace;
		this.prediction = prediction;
		this.startDate = startdate;
		this.endDate = enddate;
	}
	public ParkingForecast(ParkingPrediction prediction,
			Timestamp startdate, Timestamp enddate){
		this.parkingPlace = prediction.getParkingPlace();
		this.prediction = prediction.getPrediction();
		this.startDate = startdate;
		this.endDate = enddate;
	}
	public ParkingForecast() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the startDate
	 */
	public Timestamp getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Timestamp getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the parkingPlace
	 */
	public ParkingPlace getParkingPlace() {
		return parkingPlace;
	}

	/**
	 * @param parkingPlace the parkingPlace to set
	 */
	public void setParkingPlace(ParkingPlace parkingPlace) {
		this.parkingPlace = parkingPlace;
	}

	/**
	 * @return the prediction
	 */
	public TSPrediction getPrediction() {
		return prediction;
	}

	/**
	 * @param prediction the prediction to set
	 */
	public void setPrediction(TSPrediction prediction) {
		this.prediction = prediction;
	}
	
		
	
}
