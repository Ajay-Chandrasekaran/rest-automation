package com.spiro.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnergyPlanOffer {
	private String createdDate;
    private String createdBy;
    private String offerCode;
    private String offerName;
    private String countryCode;
    private String countryName;
    private String offerImagePath;
    private int status;
    private String description;
    private String startDate;
    private String endDate;
}
