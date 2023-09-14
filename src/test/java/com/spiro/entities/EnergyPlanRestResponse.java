package com.spiro.entities;

import java.util.List;

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
public class EnergyPlanRestResponse {
    private String message;
    private Boolean success;
    private Integer status;
    private List<EnergyPlanResponse> response;
}
