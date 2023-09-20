package com.spiro.customerenergyplan;

import static io.restassured.RestAssured.given;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


import org.apache.http.HttpStatus;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiro.PropertiesReader;
import com.spiro.pojo.ActivateEnergyPlan;
import com.spiro.pojo.EnergyPlanFullResponse;
import com.spiro.pojo.EnergyPlanResponse;
import com.spiro.pojo.SwapHistoryPojo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;


public class CustomerPlan {
	 private final String JSONPATH="src/test/resources/customerenergyresources/";

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
	@Test
    @Order(1)
	public void putActivateEnergyPlanBycustomerId1() {

		Object convertJsonFiletoObject = convertJsonFiletoObject(JSONPATH+
				"putCustomerEnergyPlan.json");

	    RestAssured.given()
		                 .contentType(ContentType.JSON)
		                 .body(convertJsonFiletoObject)
		            .when()
		                 .put("/energy-plans")
				    .then()
				         .statusCode(HttpStatus.SC_CREATED)
				         .body("success", equalTo(true))
				         .body("message", equalTo("Customer successfully availed energy plan"));
	}
	
	@Test
	public void putActivateEnergyPlanBycustomerId() {
	    
	    Object convertJsonFiletoObject = convertJsonFiletoObject(JSONPATH+
                "putCustomerEnergyPlan.json");
	    
       RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(convertJsonFiletoObject)
                  .when()
                        .put("/energy-plans")
                  .then()
                        .statusCode(HttpStatus.SC_BAD_REQUEST)
                        .body("message", equalTo("Customer has already availed one plan or existing plan not cleared."));
	}
    
    @Test
    @Order(2)
    public void validatePostCustomerEnergyPlanWithParameterSize() {
    	/*
		 * ActivateEnergyPlan energyPlan = new ActivateEnergyPlan("BJC591068705",
		 * "ATHENA_PORTAL");
		 * Test case should pass when planId is not given
		 */
		RestAssured.given()
		                 .contentType(ContentType.JSON)
		                 .body(new ActivateEnergyPlan("BJC591068705", "ATHENA_PORTAL"))
		           .when()
		                 .put("/energy-plans")
		           .then()
				         .statusCode(HttpStatus.SC_BAD_REQUEST)
				         .body("message", equalTo("Selected plan does not exist"));

		// test case should fail when given random planId
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
    }
    
    @Test
    @Order(3)
    public void validateCustomerEnergyPlanSize() {
    	 List<Integer> path = 
    			  given()
    			       .contentType(ContentType.JSON)
    			  .when()
    			       .get("/energy-plans")
    			  .then()
    			       .statusCode(HttpStatus.SC_OK)
					   .body("message", equalTo("Customer fetched successfully."))
				  .extract()
				       .path("response.customerId");
					
		  assertTrue(path.size() > 0);
	}    
    
	
	@Test
	@Order(4)
	public void getCustomerEnergyPlan() throws FileNotFoundException {
		
	 Object jsonFiletoObject = convertJsonFiletoObject(JSONPATH+
				"putCustomerEnergyPlan.json");
	 
	     ActivateEnergyPlan energyPlan = convertJsontoSpecificClassType(jsonFiletoObject,ActivateEnergyPlan.class);
			      
	     EnergyPlanFullResponse data = 
	    		 given()
	    		      .contentType(ContentType.JSON)
	    		 .when()
	    		      .get("/energy-plans")
	    		 .then().statusCode(HttpStatus.SC_OK)
					  .body("message", equalTo("Customer fetched successfully."))
	             .extract()
	                  .as(EnergyPlanFullResponse.class);
	 	
	     data.getResponse().stream().forEach((r) -> {
	   if (energyPlan.getCustomerId().equals(r.getCustomerId())) {
		   System.out.println(r.getEnergyPlanInfo().getId());
		   if (r.getEnergyPlanInfo().getId()==(energyPlan.getPalnId())) {
			assertTrue(true);
		}
		   if(r.getEnergyPlanInfo().getStatus()==1) {
			   assertTrue(true);
		   }
        }
	   });
	     
	     ArrayList<EnergyPlanResponse> arrayList = new ArrayList<EnergyPlanResponse>();
	    
	     data.getResponse().stream().forEach((r) -> {
	  		   if(r.getEnergyPlanInfo().getStatus()==1){ 
	  		   arrayList.add(r);
	  		   }
	  	   });   
     
	     if (arrayList.size()==0) {
			assertTrue(true);
		}         
	}
	
	@Test
	@Order(5)
	public void postSwapHistory() {
	
		Object jsonFiletoObject = convertJsonFiletoObject(JSONPATH+
					"postcustomerswaphistory");
		
		Object jsonExpected = convertJsonFiletoObject(
				"src/test/resources/customerenergyresources/expectedswaphistoryresult");
			 
	 SwapHistoryPojo actual = 
			     given()
			          .contentType(ContentType.JSON)
			          .body(jsonFiletoObject)
		         .when()
			          .post("/swaps/history")
			     .then()
			         .body("status",equalTo(HttpStatus.SC_CREATED))
		             .body("message", equalTo("Swap history created successfully for Benin customer : 454858478747442744745"))
		         .extract()
		          .jsonPath()
		          .getObject("response", SwapHistoryPojo.class);
	      
		System.out.println(actual);
		
		SwapHistoryPojo expected = convertJsontoSpecificClassType(jsonExpected, SwapHistoryPojo.class);
		System.out.println(expected);

		assertEquals(actual, expected);	
	}
}
