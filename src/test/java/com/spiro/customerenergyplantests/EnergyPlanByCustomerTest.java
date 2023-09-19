package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

import com.spiro.utils.PropertiesReader;

public class EnergyPlanByCustomerTest {

    private static PropertiesReader propReader;

    @BeforeAll
    public static void setup() throws IOException {
        propReader = PropertiesReader.getReader();
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();
        RestAssured.basePath = "/customers";
    }

    @AfterAll
    public static void teardown() {
        RestAssured.reset();
    }

    /**
     * [GET] customers/{{customer-id}}/energy-plans
     *
     *  getting energy plan of invalid customer shoudl fail.
     */
    @Test
    public void invalidCustomerTest() {
        String customerId = "DUMMYCUSTOMERID";
        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("/{customer-id}/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("message", equalTo("Customer does not exist by given id."));
    }

    /**
     * [GET] customers/{{customer-id}}/energy-plans
     *
     * Get shoudl fail for a valid customer without an Energy plan.
     */
    @Test
    public void customerWithoutEnergyPlanTest() {
        String customerId = "1692701797-5e8a-4845-afba-06078001c492";

        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("/{customer-id}/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("message", equalTo("Customer does not exist by given id."));

    }

    /**
     * [GET] customers/{{customer-id}}/energy-plans
     *
     * Should get details of valid customer's energy plan. (Customer already has availed energy plan)
     */
    @Test
    public void customerWithEnergyPlanTest() {
        String customerId = "1692211922-b51c-470f-89ae-16d4209123a8";

        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("/{customer-id}/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.id", notNullValue())
            .body("response.energyPlanInfo.offer.countryCode", equalTo("BJ"))
            .body("response.energyPlanInfo.offer.startDate", notNullValue())
            .body("response.energyPlanInfo.offer.endDate", notNullValue());
    }
}
