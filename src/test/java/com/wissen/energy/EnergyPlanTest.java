package com.wissen.energy;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class EnergyPlanTest {

	public static int planId;

	@AfterAll
	public static void teardown() {
		RestAssured.reset();
	}

	@Test
	public void getEnergyPlans() throws JSONException, IOException {

		Response res = given().when().get("http://dev.spironet.com:8082/energy-plans").then()
				.statusCode(HttpStatus.SC_OK).log().body().extract().response();
		Path path = Paths.get("src/test/resources/energyPlan/", "allEnergyPlans.json");
		String jsonstring = res.asString();
		byte[] responseAsStringByte = jsonstring.getBytes();
		Files.write(path, responseAsStringByte);

		JsonPath jsonpath = res.jsonPath();
		String msg = jsonpath.get("message");
		Assert.assertEquals(msg, "Energy Plan is fetched successfully");
	}

	@Test
	public void getEnergyPlanById() throws IOException {

		int id = 34;
		Response res = given().when().get("http://dev.spironet.com:8082/energy-plans/" + id + "").then()
				.statusCode(HttpStatus.SC_OK).log().body().extract().response();
		Path path = Paths.get("src/test/resources/energyPlan/", "energyPlanResponse.json");
		String jsonstring = res.asString();
		byte[] responseAsStringByte = jsonstring.getBytes();

		Files.write(path, responseAsStringByte);

	}
	@Test
	public void patchEnergyPlan() throws IOException, JSONException {
		int id=43;
		
		//0->yet to start
		//1->activate
		//2->deactivate
		//3->defaulter -only in in customer case
		
		
		Response res = given().contentType("application/Json").accept(ContentType.JSON).body(getRequest("src/test/resources/energyPlan/patchRequest.json")).patch("http://dev.spironet.com:8082/energy-plans/" + id+"/status")
				.then()
				.log().body().extract().response();
		Path path = Paths.get("src/test/resources/energyPlan/", "patchResponse.json");
		String jsonstring = res.asString();
		byte[] responseAsStringByte = jsonstring.getBytes();

		Files.write(path, responseAsStringByte);
	}

	@Test
	public void postEnergyPlan() throws Exception {

		Response res = given().accept(ContentType.JSON).contentType("application/Json")
				.body(getRequest("src/test/resources/energyPlan/energyPlanRequest.json")).when()
				.post("http://dev.spironet.com:8082/energy-plans").then().statusCode(HttpStatus.SC_CREATED).log().body()
				.extract().response();
		String jsonstring = res.asString();
		EnergyResponse response = getResponse(jsonstring);
		Assert.assertEquals(response.getMessage(), "Energy Plan is created");
		Assert.assertEquals(response.getSuccess(), true);
		Assert.assertEquals(response.getResponse(), null);

	}

	public static EnergyResponse getResponse(String res) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		EnergyResponse readValue = new EnergyResponse();
		readValue = mapper.readValue(res, EnergyResponse.class);
		return readValue;
	}

	public static String getRequest(String path) throws IOException {
		byte[] requestBytes;
		String requestString = null;

		requestBytes = Files.readAllBytes(Path.of(path));
		requestString = new String(requestBytes);

		return requestString;
	}
}
