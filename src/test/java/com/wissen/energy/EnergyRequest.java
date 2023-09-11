package com.wissen.energy;

public class EnergyRequest {
	private String lastUpdatedDate;
	private String lastUpdatedBy;
	private String createdDate;
	private String createdBy;
	private float planTotalValue;
	private String planName;
	private String planCode;
	private float swapCount;
	private float dailyPlanValue;
	private String dialCode;
	private String locationId;
	private String endDate;
	private String startDate;

	// Getter Methods

	public String getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public float getPlanTotalValue() {
		return planTotalValue;
	}

	public String getPlanName() {
		return planName;
	}

	public String getPlanCode() {
		return planCode;
	}

	public float getSwapCount() {
		return swapCount;
	}

	public float getDailyPlanValue() {
		return dailyPlanValue;
	}

	public String getDialCode() {
		return dialCode;
	}

	public String getLocationId() {
		return locationId;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getStartDate() {
		return startDate;
	}

	// Setter Methods

	public void setLastUpdatedDate(String lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setPlanTotalValue(float planTotalValue) {
		this.planTotalValue = planTotalValue;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public void setSwapCount(float swapCount) {
		this.swapCount = swapCount;
	}

	public void setDailyPlanValue(float dailyPlanValue) {
		this.dailyPlanValue = dailyPlanValue;
	}

	public void setDialCode(String dialCode) {
		this.dialCode = dialCode;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

}
