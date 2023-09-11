package com.wissen;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;


public class EnergyPlan {

	/*
	 * @BeforeAll public static void setup() throws IOException { PropertiesReader
	 * propReader = PropertiesReader.getReader(); // RestAssured.baseURI =
	 * propReader.getEnergy_url(); //RestAssured.port = propReader.getPort(); }
	 * 
	 * @AfterAll public static void teardown() { RestAssured.reset(); }
	 * 
	 * 
	 * 
	 * @Test public void getEnergyPlanTestCase() {
	 * 
	 * given() .accept(ContentType.ANY) .contentType(ContentType.ANY) .when()
	 * .get("http://dev.spironet.com:8080/energy-plans") .then()
	 * .statusCode(HttpStatus.SC_OK);
	 * 
	 * }
	 * 
	 * @Test void postEnergyPLan() { given() .accept(ContentType.JSON)
	 * .contentType(ContentType.JSON) .when()
	 * .post("http://dev.spironet.com:8080/energy-plans") .then()
	 * .statusCode(HttpStatus.SC_OK);
	 * 
	 * }
	 */}
