package com.spiro.energyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;


public class GetAllEnergyPlansTest {

    private final String SCHEMAPATH = "jsonschemas/";

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
