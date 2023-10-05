package com.spiro.energyplantests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.DeactivateEnergyPlanRequest;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.EnergyPlanResponse1;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;

public class ActivateDeactivateEnergyPlanTest {

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

    /*
     * [PATCH] /energy-plans/{{energy-plan-id}}/status
     *
     * Tries to deactivate energy plan that has an customer
     */
    @Test
    public void deactivatePlanWithCustomerTest() {
        String customerId = ObjectAndJsonUtils.UUIDgenerator();
        int energyPlanId = 1010;

        try {
            // Activate a plan for customer
            ActivatePlanForCustomer activationReq = new ActivatePlanForCustomer(energyPlanId, customerId);
            Response planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, activationReq);
            assertTrue(planActivated.jsonPath().getBoolean("success"), "Energy plan activatoin failed for customer: " + customerId);

            // Test deactivation
            DeactivateEnergyPlanRequest deactivateReq = new DeactivateEnergyPlanRequest();
            deactivateReq.setCreatedBy("ATHENA_PORTAL");
            deactivateReq.setLastUpdatedBy("ATHENA_PORTAL");
            deactivateReq.setStatus(1); // to deactivate

            given()
                .pathParam("id", energyPlanId)
                .contentType(ContentType.JSON)
                .body(deactivateReq)
            .when()
                .patch("/energy-plans/{id}/status")
            .then()
                .statusCode(HttpStatus.SC_OK) // TODO: Should it be 400 instead ?
                .body("success", equalTo(false))
                .body("message", startsWith("Energy Plan has not been deactivated"));
        } finally {
            EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        }
    }

    @Test
    public void deactivateEnergyPlanWithoutCustomerTest() throws IOException {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();
        int totalValue = 1000;

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(0);
        reqBody.setPlanTotalValue(totalValue);

        EnergyPlanResponse1 energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, reqBody);
        assertNotEquals(-1, energyPlanId.getResponse().getId(), "Energy plan creation failed");

        DeactivateEnergyPlanRequest deactivateReq = new DeactivateEnergyPlanRequest();
            deactivateReq.setCreatedBy("ATHENA_PORTAL");
            deactivateReq.setLastUpdatedBy("ATHENA_PORTAL");
            deactivateReq.setStatus(1); // to deactivate

            given()
                .pathParam("id", energyPlanId.getResponse().getId())
                .contentType(ContentType.JSON)
                .body(deactivateReq)
            .when()
                .patch("/energy-plans/{id}/status")
            .then()
                .statusCode(HttpStatus.SC_OK) // TODO: Should it be 400 instead ?
                .body("success", equalTo(true))
                .body("message", startsWith("Energy Plan has been deactivated"));
    }

    @Test
    public void ActivateInvalidPlanTest() throws IOException {
        int energyPlanId = -1;

        DeactivateEnergyPlanRequest deactivateReq = new DeactivateEnergyPlanRequest();
        deactivateReq.setCreatedBy("ATHENA_PORTAL");
        deactivateReq.setLastUpdatedBy("ATHENA_PORTAL");
        deactivateReq.setStatus(0); // to activate

        given()
            .pathParam("id", energyPlanId)
            .contentType(ContentType.JSON)
            .body(deactivateReq)
        .when()
            .patch("/energy-plans/{id}/status")
        .then()
            .statusCode(HttpStatus.SC_OK) // TODO: Should it be 400 instead ?
            .body("success", equalTo(false))
            .body("message", startsWith("Energy Plan not found for given Id."));
    }

    @Test
    public void ActivatePlanYetToStartTest() throws IOException {
        String startDate = LocalDate.now().plusDays(5).toString();
        String endDate = LocalDate.now().plusDays(15).toString();
        int totalValue = 1000;

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(0);
        reqBody.setPlanTotalValue(totalValue);

        EnergyPlanResponse1 energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, reqBody);
        assertNotEquals(-1, energyPlanId.getResponse().getId(), "Energy plan creation failed");

        DeactivateEnergyPlanRequest deactivateReq = new DeactivateEnergyPlanRequest();
        deactivateReq.setCreatedBy("ATHENA_PORTAL");
        deactivateReq.setLastUpdatedBy("ATHENA_PORTAL");
        deactivateReq.setStatus(0); // to deactivate

        given()
            .pathParam("id", energyPlanId.getResponse().getId())
            .contentType(ContentType.JSON)
            .body(deactivateReq)
        .when()
            .patch("/energy-plans/{id}/status")
        .then()
            .statusCode(HttpStatus.SC_OK) // TODO: Should it be 400 instead ?
            .body("success", equalTo(true))
            .body("message", startsWith("Energy Plan has been activated"));
    }

    @Test
    public void ActivateDeactivatedTest() throws IOException {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();
        int totalValue = 1000;

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(0);
        reqBody.setPlanTotalValue(totalValue);

        EnergyPlanResponse1 energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, reqBody);
        assertNotEquals(-1, energyPlanId.getResponse().getId(), "Energy plan creation failed");

        DeactivateEnergyPlanRequest deactivateReq = new DeactivateEnergyPlanRequest();
        deactivateReq.setCreatedBy("ATHENA_PORTAL");
        deactivateReq.setLastUpdatedBy("ATHENA_PORTAL");
        deactivateReq.setStatus(1); // to deactivate

        given()
            .pathParam("id", energyPlanId.getResponse().getId())
            .contentType(ContentType.JSON)
            .body(deactivateReq)
        .when()
            .patch("/energy-plans/{id}/status")
        .then()
            .statusCode(HttpStatus.SC_OK) // TODO: Should it be 400 instead ?
            .body("success", equalTo(true));

        deactivateReq.setStatus(0);
        given()
            .pathParam("id", energyPlanId.getResponse().getId())
            .contentType(ContentType.JSON)
            .body(deactivateReq)
        .when()
            .patch("/energy-plans/{id}/status")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(false));
    }
}
