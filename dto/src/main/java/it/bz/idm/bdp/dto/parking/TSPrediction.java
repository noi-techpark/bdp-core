package it.bz.idm.bdp.dto.parking;



public class TSPrediction{

	private Integer predictedFreeSlots;
	private double upperConfidenceLevel;
	private double lowerConfidenceLevel;
	private String status;

	public Integer getPredictedFreeSlots() {
		return predictedFreeSlots;
	}

	
	public String getStatus() {
		return this.status;
	}

	public double getUpperConfidenceLevel() {
		return upperConfidenceLevel;
	}

	public double getLowerConfidenceLevel() {
		return lowerConfidenceLevel;
	}
}
