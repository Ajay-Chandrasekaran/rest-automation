package com.spiro.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EnergyPlan {
    private String lastUpdatedBy;
    private String createdBy;
    private Integer planTotalValue;
    private String planName;
    private String planCode;
    private Integer swapCount;
    private Integer dailyPlanValue;
    private String dialCode;
    private String locationId;
    private String endDate;
    private String startDate;
}
