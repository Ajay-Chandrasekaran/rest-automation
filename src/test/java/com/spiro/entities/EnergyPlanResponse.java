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
public class EnergyPlanResponse {
    private String startDate;
    private String endDate;
    private Integer status;
    private Integer availedSwaps;
    private Integer totalAmountPaid;
    private Integer daysPaid;
    private Integer missedDays;
    private Double overdueAmount;
    private EnergyPlanInfo energyPlanInfo;
}
