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
