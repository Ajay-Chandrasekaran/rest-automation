package com.spiro.customerenergyplantests;

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
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;

public class EnergyPlanByCountryTest {

    private final String PATH = "src/test/resources/customerenergyplantests/";
    private PropertiesReader propReader;

    @BeforeAll
    public void setup() throws IOException {
        propReader = PropertiesReader.getReader();
        propReader.useDevEnv();
    }

    @AfterAll
    public void teardown() {
        RestAssured.reset();
    }

    /**
     * API: [GET]/customers/{customer-id}/{country-code}
     *
     * Creates new Energy plan for Kenya (+254)
     * Verify if this energy plan is getting listed
     */
    @Test
    public void checkEnergyPlanLocTest() throws IOException {
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        int swapCount = 5;
        String locationId = "1686137320-5524-4314-bc0f-9ba65f088a58";

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(PATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(swapCount);
        reqBody.setLocationId(locationId);

        Integer energyPlanId = given()
            .header("Content-Type", ContentType.JSON)
            .body(reqBody)
        .when()
            .post("/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_CREATED)
        .extract()
            .path("response.id");

        // value of customerId doesn't affect the response
        int customerId = 0;
        int dialCode = 254;

        given()
        .when()
            .get("/customers/"+ customerId + "/" + dialCode)
        .then()
            .body("success", equalTo(true))
            .body("response.find { it.id = " + energyPlanId + " }.id", equalTo(energyPlanId))
            .body("response[0].offer.countryCode",equalTo("KE"));
    }

    /**
     * API: [GET]/energy-plans/{{energy-plan-id}}
     *
     * Creates new Energy plan for Kenya (+254)
     * Verify if this energy plan is getting listed
     */
    @Test
    public void getEnergyByPlanLocTest() throws IOException {
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        int swapCount = 5;
        String locationId = "1686137320-5524-4314-bc0f-9ba65f088a58";

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(PATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(swapCount);
        reqBody.setLocationId(locationId);

        Integer energyPlanId = given()
            .header("Content-Type", ContentType.JSON)
            .body(reqBody)
        .when()
            .post("/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_CREATED)
        .extract()
            .path("response.id");

        given()
        .when()
            .get("/energy-plans/" + energyPlanId)
        .then()
            .body("success", equalTo(true))
            .body("response.id", equalTo(energyPlanId))
            .body("response.offer.status", equalTo(1))
            .body("response.offer.startDate", equalTo(startDate))
            .body("response.offer.endDate", equalTo(endDate))
            .body("response.swapCount", equalTo(swapCount));
    }
}
