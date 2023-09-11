package com.wissen.energy;

import java.util.ArrayList;

public class EnergyResponse {
	private String message;
	 private boolean success;
	 private float status;
	 ArrayList<Object> response =new ArrayList<>();


	 // Getter Methods 

	 public String getMessage() {
	  return message;
	 }

	 public boolean getSuccess() {
	  return success;
	 }

	 public float getStatus() {
	  return status;
	 }

	

	 // Setter Methods 

	 public ArrayList<Object> getResponse() {
		return response;
	}

	public void setResponse(ArrayList<Object> response) {
		this.response = response;
	}

	public void setMessage(String message) {
	  this.message = message;
	 }

	 public void setSuccess(boolean success) {
	  this.success = success;
	 }

	 public void setStatus(float status) {
	  this.status = status;
	 }

	
	
}
