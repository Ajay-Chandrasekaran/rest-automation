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
public class EnergyPlanOffer {
    private String createdDate;
    private String createdBy;
    private String offerCode;
    private String offerName;
    private String countryCode;
    private String countryName;
    private String offerImagePath;
    private Integer status;
    private String description;
    private String startDate;
    private String endDate;
}
