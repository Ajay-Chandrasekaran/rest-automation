package com.spiro.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
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
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivateEnergyPlan {

	 @JsonAlias({ "id","planId" })
	private int palnId;
	private String customerId;
	private String createdBy;

	public ActivateEnergyPlan(int palnId, String createdBy) {
		super();
		this.palnId = palnId;
		this.createdBy = createdBy;
	}

	public ActivateEnergyPlan(String customerId, String createdBy) {
		super();
		this.customerId = customerId;
		this.createdBy = createdBy;
	}

}
