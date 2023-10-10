package com.spiro.energyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.DeactivateEnergyPlanRequest;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.EnergyPlanResponse1;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.CsvUtils;
import com.spiro.utils.ObjectAndJsonUtils;

public class ActivateDeactivateEnergyPlanTest {

    private static final Logger logger = LogManager.getLogger();
    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    /*
     * [PATCH] /energy-plans/{{energy-plan-id}}/status
     *
     * Tries to deactivate energy plan that has an customer
     */
    @Test
    public void deactivatePlanWithCustomerTest() {
        String customerId = CsvUtils.getNextCustomer();
        int energyPlanId = 1010;
        
        try {
            // Activate a plan for customer
            ActivatePlanForCustomer activationReq = new ActivatePlanForCustomer(energyPlanId, customerId);
            Response planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(activationReq);

            if (planActivated.jsonPath().getInt("response.status") != HttpStatus.SC_CREATED) {
                logger.error("Activation of plan {} failed for customer {}", energyPlanId, customerId);
                fail("Energy plan activation failed for customer " + customerId);
            }
            logger.info("Activated plan {} for customer {}", energyPlanId, customerId);

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
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(false))
                .body("message", startsWith("Energy Plan has not been deactivated"));
        } finally {
            EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(customerId);
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

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(reqBody).jsonPath().getInt("response.id");
        assertNotEquals(-1, energyPlanId, "Energy plan creation failed");

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
                .statusCode(HttpStatus.SC_OK)
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
            .statusCode(HttpStatus.SC_OK)
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

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(reqBody).jsonPath().getInt("response.id");
        assertNotEquals(-1, energyPlanId, "Energy plan creation failed");

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
            .statusCode(HttpStatus.SC_OK)
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

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(reqBody).jsonPath().getInt("response.id");
        assertNotEquals(-1, energyPlanId, "Energy plan creation failed");

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
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(true));

        deactivateReq.setStatus(0);
        given()
            .pathParam("energy-plan-id", energyPlanId.getResponse().getId())
            .contentType(ContentType.JSON)
            .body(deactivateReq)
        .when()
            .patch("/energy-plans/{id}/status")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(false));
    }
}
