package com.spiro.customerenergyplantests;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotEquals;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.Payment;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.CsvUtils;
import com.spiro.utils.ObjectAndJsonUtils;

public class DeactivateEnergyPlanForCustomerTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    @BeforeClass
    public static void setup() throws IOException {
        EnergyPlanTestHelper.init();
    }

    @Test
    public void deactivateEnergyPlanForCustomerTest() throws IOException {
        String customerId = CsvUtils.getNextCustomer();
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
        String customerId = CsvUtils.getNextCustomer();
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
        String customerId = CsvUtils.getNextCustomer();
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
