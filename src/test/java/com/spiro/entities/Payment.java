package com.spiro.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {
    private String lastUpdatedBy;
    private String createdBy;
    private String customerId;
    private int offerId;
    private String offerType;
    private float offerEmi;
    private String emiDate;
    private float settlementAmount;
    private String status="payment-paid";
    private String locationCode;
    private String currency;
    private String vehicleStatus;
}
