package com.spiro.entities;

import java.util.List;

import groovy.transform.EqualsAndHashCode;
import lombok.AllArgsConstructor;
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
public class EnergyPlanGetResponse {
    private String message;
    private Boolean success;
    private Integer status;
    private List<EnergyPlanResponse> response;
}
