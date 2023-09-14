package com.spiro.energy;

import lombok.Data;

@Data
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

}