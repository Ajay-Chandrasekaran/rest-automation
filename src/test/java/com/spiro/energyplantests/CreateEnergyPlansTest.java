package com.spiro.energyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
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
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;


public class CreateEnergyPlansTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

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
     * [POST] {{host}}/energy-plans
     *
     * Verifies creation of energy plans
     *
     * Expected: Energy plan created
     */
    @Test
    public void createValidEnergyPlanTest() throws IOException {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        int swapCount = 5;
        String locationId = "1686137320-5524-4314-bc0f-9ba65f088a58";

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(swapCount);
        reqBody.setLocationId(locationId);

        given()
            .header("Content-Type", ContentType.JSON)
            .body(reqBody)
        .when()
            .post("/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_CREATED)
            .body("response.id", greaterThan(0))
            .body("response.offer.status", equalTo(1)) // Status should be active
            .body("response.swapCount", equalTo(swapCount));
    }

    /**
     * [POST] {{host}}/energy-plans
     *
     * Create energy plan with invalid swap and totalPlanValue
     *
     * Expected: Creation should fail
     */
    @Test
    public void createEnergyPlanWithInvalidValueTest() throws IOException {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        int swapCount = -1; // Invalid swap count
        int totalValue = -40; // Invalid total value
        String locationId = "1686137320-5524-4314-bc0f-9ba65f088a58";

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(swapCount);
        reqBody.setLocationId(locationId);
        reqBody.setPlanTotalValue(totalValue);

        given()
            .header("Content-Type", ContentType.JSON)
            .body(reqBody)
        .when()
            .post("/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("success", equalTo(false));
    }

    /**
     * [POST] {{host}}/energy-plans
     *
     * Create energy plan with invalid start & end date
     *
     * Expected: Creation should fail
     */
    @Test
    public void createEnergyPlanWithInvalidDateTest() throws IOException {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().minusDays(5).toString(); // end date is before start date

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);

        given()
            .header("Content-Type", ContentType.JSON)
            .body(reqBody)
        .when()
            .post("/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("success", equalTo(false));
    }
}
