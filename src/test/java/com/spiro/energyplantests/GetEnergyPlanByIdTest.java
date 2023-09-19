package com.spiro.energyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import com.spiro.entities.EnergyPlan;
import com.spiro.utils.PropertiesReader;


public class GetEnergyPlanByIdTest {

    @BeforeAll
    public static void setup() throws IOException {
        PropertiesReader propReader = PropertiesReader.getReader();
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();
    }

    @AfterAll
    public static void teardown() {
        RestAssured.reset();
    }

    /**
     * [GET] /energy-plans/{energy-plan-id}
     *
     * get details of energy plan
     *
     * Expected: Response should have all the required details
     */
    @Test
    public void getvalidPlanByIdTest() {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        int swapCount = 10;

        EnergyPlan reqBody = new EnergyPlan();
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(swapCount);
        reqBody.setPlanName("Energy plan for TOGO");
        reqBody.setPlanCode("AUTO_TG_EPLAN");
        reqBody.setPlanTotalValue(5000);
        reqBody.setDailyPlanValue(500);
        reqBody.setLocationId("1632224482-cfae-4eaa-a451-bd26f0df4a41"); // TOGO
        reqBody.setDialCode("229"); // TOGO

        // create a new energy plan
        int energyPlanId = given()
            .header("Content-Type", ContentType.JSON)
            .body(reqBody)
        .when()
            .post("/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_CREATED)
        .extract()
            .path("response.id");

        // get the created energy plan by its id
        given()
            .pathParam("energy-plan-id", energyPlanId)
        .when()
            .get("/energy-plans/{energy-plan-id}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(true))
            .body("response.offer.startDate", equalTo(startDate))
            .body("response.offer.endDate", equalTo(endDate))
            .body("response.offer.countryCode", equalTo("TG"))
            .body("response.swapCount", equalTo(swapCount));
    }

    /**
     * [GET] /energy-plans/{energy-plan-id}
     *
     * Tries to get details of invalid energy plan
     *
     * Expected: 400 bad request
     */
    @Test
    public void getInvalidPlanByIdTest() {
        int energyPlanId = -1; // invalid id
        given()
            .pathParam("energy-plan-id", energyPlanId)
        .when()
            .get("/energy-plans/{energy-plan-id}")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("success", equalTo(false));
    }
}
