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
public class EnergyPlanInfo {
    private Integer id;
    private Integer planDuration;
    private String planType;
    private Integer monthlyPackage;
    private Integer swapCount;
    private Integer dailyPayValue;
    private String percentageBenefit;
    private Integer costOnDemand;
    private Integer costSaving;
    private Integer status;
    private Integer isAvailed;
    private EnergyPlanOffer offer;
}
