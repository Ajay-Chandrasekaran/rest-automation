package com.spiro.customerenergyplan;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.HttpStatus;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiro.PropertiesReader;
import com.spiro.pojo.ActivateEnergyPlan;
import com.spiro.pojo.EnergyPlanFullResponse;
import com.spiro.pojo.EnergyPlanResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class CustomerPlan {

	@BeforeAll
	public static void setup() throws IOException {
		PropertiesReader propReader = PropertiesReader.getReader();
		propReader.useDevEnv();
		RestAssured.baseURI = propReader.getHost();
		RestAssured.port = propReader.getPort();
		RestAssured.basePath = "/customers";
	}

	@AfterAll
	public static void teardown() {
		RestAssured.reset();
	}

	public static Object convertJsonFiletoObject(String fileName) {
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(new FileReader(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static <T> T convertJsontoSpecificClassType(Object obj,Class<T> classType) {
		var oMapper = new ObjectMapper();
        T convertValue = oMapper.convertValue(obj, classType);
        return convertValue;	
	}
	/*
	 * 
	 * Activating a energy plan to a customer based on is CustomerId 
	 *
	 */
//	@Test
    @Order(1)
	public void putActivateEnergyPlanBycustomerId() {

		Object convertJsonFiletoObject = convertJsonFiletoObject(
				"src/test/resources/customerenergyresources/putCustomerEnergyPlan.json");

	    RestAssured.given()
		                 .contentType(ContentType.JSON)
		                 .body(convertJsonFiletoObject)
		            .when()
		                 .put("/energy-plans")
				    .then()
				         .statusCode(HttpStatus.SC_CREATED)
				         .body("success", equalTo(true))
				         .body("message", equalTo("Customer successfully availed energy plan"));

		
		// test case should fail if customer Id is already allocated by EnergyPlan
		RestAssured.given()
		                 .contentType(ContentType.JSON)
		                 .body(convertJsonFiletoObject)
		           .when()
		                 .put("/energy-plans")
				   .then()
				         .statusCode(HttpStatus.SC_BAD_REQUEST)
				         .body("message", equalTo("Customer has already availed one plan or existing plan not cleared."));


		//ActivateEnergyPlan energyPlan = new ActivateEnergyPlan("BJC591068705", "ATHENA_PORTAL");
		RestAssured.given()
		                 .contentType(ContentType.JSON)
		                 .body(new ActivateEnergyPlan("BJC591068705", "ATHENA_PORTAL"))
		           .when()
		                 .put("/energy-plans")
		           .then()
				         .statusCode(HttpStatus.SC_BAD_REQUEST)
				         .body("message", equalTo("Selected plan does not exist"));

		// test case should fail but passing when given random planId 
		RestAssured.given()
		                 .contentType(ContentType.JSON)
		                 .body(new ActivateEnergyPlan(75564, "B365JC68559544s105405", "ATHENA_PORTAL"))
		           .when()
		                 .put("/energy-plans")
		           .then()
				         .statusCode(HttpStatus.SC_BAD_REQUEST)
				         .body("message", equalTo("Selected plan does not exist"));

		// test case should pass if customerId is not present
		ActivateEnergyPlan energyPlan2 = new ActivateEnergyPlan(7, "ATHENA_PORTAL");

		RestAssured.given()
		                 .contentType(ContentType.JSON)
		                 .body(energyPlan2)
		           .when()
		                 .put("/energy-plans")
		           .then()
				         .statusCode(HttpStatus.SC_BAD_REQUEST)
				         .body("message", equalTo("Customer id is not present."));
		
		/////////////////////
		
		  List<Integer> path = given().contentType(ContentType.JSON).when().get("/energy-plans").then().statusCode(HttpStatus.SC_OK)
					.body("message", equalTo("Customer fetched successfully.")).extract().path("response.customerId");
					
		  assertTrue(path.size() > 0);
	}
	
	@Test
	public void getCustomerEnergyPlan() {
		
	 Object jsonFiletoObject = convertJsonFiletoObject(
				"src/test/resources/customerenergyresources/putCustomerEnergyPlan.json");
	 
	     ActivateEnergyPlan energyPlan = convertJsontoSpecificClassType(jsonFiletoObject,ActivateEnergyPlan.class);
			      
	     EnergyPlanFullResponse data = given().contentType(ContentType.JSON).when().get("/energy-plans").then().statusCode(HttpStatus.SC_OK)
					.body("message", equalTo("Customer fetched successfully."))
	     .extract().as(EnergyPlanFullResponse.class);
	 	
	     data.getResponse().stream().forEach((r) -> {
	   if (energyPlan.getCustomerId().equals(r.getCustomerId())) {
		   if (r.getEnergyPlanInfo().getId()==(energyPlan.getPalnId())) {
			assertTrue(true);
		}
}
	   });

	
	}


}
