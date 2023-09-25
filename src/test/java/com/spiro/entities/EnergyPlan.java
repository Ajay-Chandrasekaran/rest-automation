package com.spiro.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnergyPlan {
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
