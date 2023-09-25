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
public class EnergyPlanOffer {
    private String createdDate;
    private String createdBy;
    private String offerCode;
    private String offerName;
    private String countryCode;
    private String countryName;
    private Integer status;
    private String description;
    private String startDate;
    private String endDate;
}
