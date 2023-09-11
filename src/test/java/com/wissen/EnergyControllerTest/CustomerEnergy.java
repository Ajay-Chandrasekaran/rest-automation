package com.wissen.EnergyControllerTest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.wissen.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class CustomerEnergy {

	@BeforeAll
	public static void setup() throws IOException {
		PropertiesReader propReader = PropertiesReader.getReader();
		propReader.userDev();
		RestAssured.baseURI = propReader.getHost();
		RestAssured.port = propReader.getPort();
	}

	@AfterAll
	public static void teardown() {
		RestAssured.reset();
	}

	@Test
	void getCustomerEnergyPlanTest() {
		given().contentType(ContentType.JSON).when().get("/customers/energy-plans").then().statusCode(HttpStatus.SC_OK)
				.body("message", equalTo("Customer fetched successfully."));

	}

	@Test
	void putCustomeEnergyPlanTest() throws FileNotFoundException {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("/Users/User/Desktop/course.json"));
			JSONObject jsonObject = (JSONObject) obj;

			given().contentType(ContentType.JSON).body(jsonObject).when().put("/customers/energy-plans").then()
					.statusCode(HttpStatus.SC_NOT_FOUND)
					.body("message", equalTo("Customer has already availed one plan or existing plan not cleared."));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

}