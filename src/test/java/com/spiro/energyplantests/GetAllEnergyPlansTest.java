package com.spiro.energyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

import com.spiro.utils.PropertiesReader;


public class GetAllEnergyPlansTest {

    private final String SCHEMAPATH = "jsonschemas/";

    @BeforeAll
    public static void setup() throws IOException {
        PropertiesReader propReader = PropertiesReader.getReader();
        propReader.setEnv("dev");
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();
    }

    @AfterAll
    public static void teardown() {
        RestAssured.reset();
    }

    /**
     * [GET] /energy-plans
     *
     * Get all energy plans
     *
     * Expected: retireves energy plan
     */
    @Test
    public void getAllPlansTest() throws IOException {
        given()
        .when()
            .get("/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(true))
            .body(matchesJsonSchemaInClasspath(SCHEMAPATH + "energyplanResponse-schema.json"));
    }
}
