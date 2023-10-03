package com.spiro.energyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;

import com.spiro.helpers.EnergyPlanTestHelper;


public class GetAllEnergyPlansTest {

    private final String SCHEMAPATH = "jsonschemas/";

    @BeforeClass
    public static void setup() throws IOException {
        EnergyPlanTestHelper.init();
    }

    @AfterClass
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
