package com.spiro.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {
    private String lastUpdatedBy;
    private String createdBy;
    private String customerId;
    private int offerId;
    private String offerType;
    private int offerEmi;
    private String emiDate;
    private int settlementAmount;
    private String status;
    private String locationCode;
    private String currency;
    private String vehicleStatus;
}
