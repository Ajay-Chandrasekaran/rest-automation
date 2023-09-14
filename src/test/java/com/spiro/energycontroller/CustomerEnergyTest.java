package com.spiro.energycontroller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.spiro.PropertiesReader;
import com.spiro.pojo.SwapHistoryPojo;

public class CustomerEnergyTest {

	@BeforeAll
	public static void setup() throws IOException {
		PropertiesReader propReader = PropertiesReader.getReader();
		propReader.useDevEnv();
		RestAssured.baseURI = propReader.getHost();
		RestAssured.port = propReader.getPort();
	}

	@AfterAll
	public static void teardown() {
		RestAssured.reset();
	}

	@Test
	public void getCustomerEnergyPlanTest() {
		given().contentType(ContentType.JSON).when().get("/customers/energy-plans").then().statusCode(HttpStatus.SC_OK)
				.body("message", equalTo("Customer fetched successfully."));
	}

	@Test
	public void postSwapHistory() throws FileNotFoundException, IOException, ParseException, JSONException {
		JSONParser parser = new JSONParser();
		Object expected = parser.parse(new FileReader("src/test/resources/customerenergyplan/jsonswaphistory.json"));

		Object obj = parser.parse(new FileReader("src/test/resources/customerenergyplan/PostJsonSwapHistory.json"));
		JSONObject jsonObject = (JSONObject) obj;

		Object path = given().contentType(ContentType.JSON).body(jsonObject).when().post("/customers/swaps/history")
				.then().statusCode(HttpStatus.SC_OK)
				.body("message", equalTo("Swap history created successfully for Benin customer : BJC534910013"))

				.extract().path("response");

		ObjectMapper objectMapper = new ObjectMapper();
		SwapHistoryPojo actual = objectMapper.convertValue(path, SwapHistoryPojo.class);

		SwapHistoryPojo expectedres = objectMapper.convertValue(expected, SwapHistoryPojo.class);

		assertEquals(expectedres, actual);
	}

	@Test
	public void putCustomeEnergyPlanTest() throws FileNotFoundException {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser
					.parse(new FileReader("src/test/resources/customerenergyplan/putCustomerEnergyPlan.json"));
			JSONObject jsonObject = (JSONObject) obj;

			given().contentType(ContentType.JSON).body(jsonObject).when().put("/customers/energy-plans").then()
					.body("message", equalTo("Customer has already availed one plan or existing plan not cleared."));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getCustomerByIdTest() {
		given().pathParam("customer-id", "12345").contentType(ContentType.JSON).when()
				.get("/customers/{customer-id}/energy-plans").then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void getCustomersRemainingAmount() {
		given().pathParam("customer-id", "BJC534910013").contentType(ContentType.JSON).when()
				.get("/customers/energy-plan-remaining-amount/{customer-id}").then().statusCode(HttpStatus.SC_OK)
				.body("message", equalTo("Fetched customer remaining due amount Of energy plan"));
	}

	@Test
	public void postCustomerSwapHistoryTest() throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();

		Object parse = parser
				.parse(new FileReader("src/test/resources/customerenergyplan/customerpaymenthistory.json"));

		given().contentType(ContentType.JSON).body(parse).when().post("/customers/payments/history").then()
				.statusCode(HttpStatus.SC_CREATED)
				.body("message[0]", equalTo("Payment history created successfully for customer"));
	}

	@Test
	public void getCustomerSwapHistoryTest() {
		given().pathParam("customer-id", "BJC534910013").contentType(ContentType.JSON).when()
				.get("/customers/payment-history/{customer-id}").then().statusCode(HttpStatus.SC_OK)
				.body("message", equalTo("Fetched energy plan payment history."));
	}

	@Test
	public void getEnergyPlanBycustomerIdDialCodeTest() {
		given().pathParam("customer-id", "BJC534910013").pathParam("country-code", "229").contentType(ContentType.JSON)
				.when().get("/customers/{customer-id}/{country-code}").then().statusCode(HttpStatus.SC_OK)
				.body("message", equalTo(
						"Fetched energy plan list on the basis of dial code :: 229 and customerId :: BJC534910013"));
	}

	@Test
	public void patchCustomersIdToDeactivateEnergyPlan() {
		given().pathParam("customer-id", "BJC534910013").contentType(ContentType.JSON).when()
				.patch("/customers/{customer-id}/energy-plans").then().statusCode(HttpStatus.SC_OK);
	}

}
