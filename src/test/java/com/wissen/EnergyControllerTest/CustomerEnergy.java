package com.wissen.EnergyControllerTest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import com.wissen.PropertiesReader;

public class CustomerEnergy {

	@BeforeAll
	public static void setup() throws IOException {
		PropertiesReader propReader = PropertiesReader.getReader();
		propReader.useDev();
		RestAssured.baseURI = propReader.getHost();
		RestAssured.port = propReader.getPort();
	}

	@AfterAll
	public static void teardown() {
		RestAssured.reset();
	}

	@Test
	void getCustomerEnergyPlanTest() {
		given()
            .contentType(ContentType.JSON)
        .when()
            .get("/customers/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_OK)
			.body("message", equalTo("Customer fetched successfully."));
	}

	@Test
	void putCustomeEnergyPlanTest() throws FileNotFoundException {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("src/test/resources/putCustomerEnergyPlan"));
			JSONObject jsonObject = (JSONObject) obj;

			given()
                .contentType(ContentType.JSON)
                .body(jsonObject)
            .when()
                .put("/customers/energy-plans")
            .then()
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body("message", equalTo("Customer has already availed one plan or existing plan not cleared."));
		} catch (Exception e) {
            e.printStackTrace();
		}
	}

	@Test
	void getCustomerByIdTest() {
		given().pathParam("customer-id", "12345").contentType(ContentType.JSON).when()
				.get("/customers/{customer-id}/energy-plans").then().statusCode(HttpStatus.SC_OK);

	}

	@Test
	void postSwapHistory() throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("src/test/resources/SwapHistory"));
			JSONObject jsonObject = (JSONObject) obj;

			Object obj1 = parser.parse(new FileReader("src/test/resources/customerenergyplan/jsonswaphistory"));
			JSONObject jsonObject1 = (JSONObject) obj1;

			ExtractableResponse<Response> extract = given().contentType(ContentType.JSON).body(jsonObject).when().post("/customers/swaps/history").then()
					.statusCode(HttpStatus.SC_OK)
					.body("message", equalTo("Swap history created successfully for Benin customer : BJC534910013")).extract();

			assertEquals(extract, jsonObject1);
		} catch (Exception e) {

		}
	}

}
