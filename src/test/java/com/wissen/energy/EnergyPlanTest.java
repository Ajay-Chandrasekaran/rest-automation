package com.wissen.energy;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class EnergyPlanTest {
	/*
	 * @BeforeAll public static void setup() throws IOException { PropertiesReader
	 * propReader = PropertiesReader.getReader(); // RestAssured.baseURI =
	 * propReader.getEnergy_url(); //RestAssured.port = propReader.getPort(); }
	 */

	@AfterAll
	public static void teardown() {
		RestAssured.reset();
	}

	@Test
	public void getEnergyPlans() {

		given().accept(ContentType.ANY).contentType(ContentType.ANY).when()
				.get("http://dev.spironet.com:8080/energy-plans").then().statusCode(HttpStatus.SC_OK);

	}
	//the below code block is not completed yet
	@Test
	public void getEnergyPlanById() {

		given().accept(ContentType.ANY).contentType(ContentType.ANY).when()
				.get("http://dev.spironet.com:8080/energy-plans").then().statusCode(HttpStatus.SC_OK);

	}

	@Test
	void postEnergyPLan() {

		Response res = given().accept(ContentType.JSON).contentType("application/Json").body(getRequest()).when()
				.post("http://dev.spironet.com:8080/energy-plans").then().statusCode(HttpStatus.SC_OK).log().body()
				.extract().response();
		String jsonstring = res.asString();
		EnergyResponse response = getResponse(jsonstring);

		Assert.assertEquals(response.getMessage(), "Energy Plan is created");
		Assert.assertEquals(response.getStatus(), "201");
		Assert.assertEquals(response.getSuccess(), true);
		Assert.assertEquals(response.getResponse(), null);

	}

	public static EnergyResponse getResponse(String res) {
		ObjectMapper mapper = new ObjectMapper();
		EnergyResponse readValue = new EnergyResponse();
		try {
			readValue = mapper.readValue(res, EnergyResponse.class);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readValue;

	}

	public static String getRequest() {
		byte[] requestBytes;
		String requestString = null;
		try {
			requestBytes = Files.readAllBytes(Paths.get("energyPlanRequest.json"));
			requestString = new String(requestBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return requestString;
	}
}
