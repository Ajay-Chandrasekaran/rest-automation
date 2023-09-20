package com.spiro.energyplantests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.DeactivateEnergyPlanRequest;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class ActivateDeactivateEnergyPlanTest {

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
        // TODO: Remove hardcoded plan value
        String customerId = "1683292260-0bf8-4bdf-aad0-5c4fc62cb619";
        int energyPlanId = 266;

        try {
            // Activate a plan for customer
            ActivatePlanForCustomer activationReq = new ActivatePlanForCustomer(energyPlanId, customerId);
            boolean planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, activationReq);
            assertTrue(planActivated, "Energy plan activatoin failed for customer: " + customerId);

            // Test deactivation
            DeactivateEnergyPlanRequest deactivateReq = new DeactivateEnergyPlanRequest();
            deactivateReq.setCreatedBy("ATHENA_PORTAL");
            deactivateReq.setLastUpdatedBy("ATHENA_PORTAL");
            deactivateReq.setStatus(1); // to deactivate

            given()
                .pathParam("energy-plan-id", energyPlanId)
                .contentType(ContentType.JSON)
                .body(deactivateReq)
            .when()
                .patch("/energy-plans/{energy-plan-id}/status")
            .then()
                .statusCode(HttpStatus.SC_OK) // TODO: Should it be 400 instead ?
                .body("success", equalTo(false))
                .body("message", startsWith("Energy Plan has not been deactivated"));
        } finally {
            EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        }
    }
}
