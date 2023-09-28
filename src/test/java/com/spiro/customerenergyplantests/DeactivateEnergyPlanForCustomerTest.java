package com.spiro.customerenergyplantests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.Payment;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;

public class DeactivateEnergyPlanForCustomerTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    @Test
    public void deactivateEnergyPlanForCustomerTest() throws IOException {
        String customerId = "1682401971-dee5-42b0-8b5b-395870cdea15";
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(0);
        reqBody.setPlanTotalValue(0);

        int planId = EnergyPlanTestHelper.createEnergyPlan(reqBody);
        assertNotEquals(-1, planId, "Error while creating energy plan");

        ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(planId, customerId);
        boolean planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(activateReq);
        assertTrue(planActivated, "Error while activating plan for customer: " + customerId);

        given()
            .pathParam("customer-id", customerId)
        .when()
            .patch("/customers/{customer-id}/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(true));
    }

    @Test
    public void deactivateEnergyPlanForCustomerWithDueTest() throws IOException {
        String customerId = "1657618561-1597-4dc4-ae35-f48d2f4e45e5";
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();
        int totalValue = 1000;

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(0);
        reqBody.setPlanTotalValue(totalValue);

        int planId = EnergyPlanTestHelper.createEnergyPlan(reqBody);
        assertNotEquals(-1, planId, "Error while creating energy plan");

        try {
            ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(planId, customerId);
            boolean planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(activateReq);
            assertTrue(planActivated, "Error while activating plan for customer: " + customerId);

            given()
                .pathParam("customer-id", customerId)
            .when()
                .patch("/customers/{customer-id}/energy-plans")
            .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(false));
        } finally {
            Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
            payment.setOfferId(planId);
            payment.setCustomerId(customerId);
            payment.setSettlementAmount(totalValue);
            boolean paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(payment);
            if (!EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(customerId) || !paymentSuccess) {
                System.err.println("Energy plan(" + planId + ") deactivation for customer : " + customerId + " Failed");
            }
        }
    }

    @Test
    public void deactivateExpiredEnergyPlanTest() throws IOException {
        String customerId = "1635938770-8128-4b26-8e5b-a7ee1b21b2cf";
        String startDate = LocalDate.now().minusDays(5).toString();
        String endDate = LocalDate.now().minusDays(3).toString();
        int totalValue = 0;

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(0);
        reqBody.setPlanTotalValue(totalValue);

        int planId = EnergyPlanTestHelper.createEnergyPlan(reqBody);
        assertNotEquals(-1, planId, "Error while creating energy plan");

        try {
            ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(planId, customerId);
            boolean planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(activateReq);
            assertTrue(planActivated, "Error while activating plan for customer: " + customerId);

            given()
                .pathParam("customer-id", customerId)
            .when()
                .patch("/customers/{customer-id}/energy-plans")
            .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
        } finally {
            if (!EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(customerId)) {
                System.err.println("Energy plan(" + planId + ") deactivation for customer : " + customerId + " Failed");
            }
        }
    }
}
