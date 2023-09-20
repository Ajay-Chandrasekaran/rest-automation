package com.spiro.pojo;

import javax.sound.midi.Track;

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
public class SwapHistoryPojo {

	private String transactionId;
	private float amount;
	private String currency;
	private String payMode;
	private String batterySlot;
	private String oldBatteryUuid;
	private String newBatteryUuid;
	private String customerId;
	private String agentId;
	private String stationId;
	private String gatewayId;
	private String status;
	private String vehicleNumber;
	
	
}
