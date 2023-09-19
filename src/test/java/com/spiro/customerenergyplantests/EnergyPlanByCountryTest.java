package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import com.spiro.entities.EnergyPlan;
import com.spiro.entities.EnergyPlanOffer;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;

public class EnergyPlanByCountryTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

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
     * API: [GET]/customers/{customer-id}/{country-code}
     *
     * Creates new Energy plan for Kenya (+254)
     * Verify if this energy plan is getting listed
     */
    @Test
    public void checkEnergyPlanLocTest() throws IOException {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);

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

        EnergyPlanOffer responseOffer = given()
            .pathParam("customer-id", customerId)
            .pathParam("dialcode", dialCode)
        .when()
            .get("/customers/{customer-id}/{dialcode}")
        .then()
            .body("success", equalTo(true))
            .body("response[0].offer.countryCode",equalTo("KE"))
        .extract()
            .jsonPath().getObject("response.find { it.id == " + energyPlanId + " }.offer", EnergyPlanOffer.class);

        assertEquals(endDate, responseOffer.getEndDate());
        assertEquals(startDate, responseOffer.getStartDate());
        assertEquals("KE", responseOffer.getCountryCode());
    }

    /**
     * [GET] /customers/{customer-id}/{country-code}
     *
     * Test for invalid country code
     *
     * Expected: Should return HTTP 400
     */
    @Test
    public void getEnergyPlanForInvalidCountryTest() {
        int customerId = 0;
        int dialCode = 999; // Invalid country code

        given()
            .pathParam("customer-id", customerId)
            .pathParam("dialcode", dialCode)
        .when()
            .get("/customers/{customer-id}/{dialcode}")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
