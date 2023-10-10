package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotEquals;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.EnergyPlanResponse1;
import com.spiro.entities.Payment;
import com.spiro.utils.CsvUtils;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.helpers.EnergyPlanTestHelper;


public class EnergyPlanRemainingAmountTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    @Test
    public void getRemainingAmountTest() throws IOException {
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

        // activate the new plan for customer
        String customerId = CsvUtils.getNextCustomer();
        ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(energyPlanId, customerId);
        boolean activationSuccess = EnergyPlanTestHelper.activateEnergyPlanForCustomer(activateReq).jsonPath().getBoolean("success");
        assertTrue(activationSuccess, "Energy plan activation failed");

        // validate remaining amount after assigning plan
        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("customers/energy-plan-remaining-amount/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.remainingDueAmount", equalTo((float)totalValue));

        // ** Clean up after test **
        // pay remaining amount (pre conditoin for deactivation)
        Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
        payment.setOfferId(planId);
        payment.setCustomerId(customerId);
        payment.setSettlementAmount(totalValue);
        boolean paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(payment).jsonPath().getBoolean("success");
        assertTrue(paymentSuccess, "Payment failed");

        // validating remaining amout after complete settlement
        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("customers/energy-plan-remaining-amount/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.remainingDueAmount", equalTo((float)0));

        // Deactivate energy plan
        boolean deactivationSuccess = EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(customerId).jsonPath().getBoolean("success");
        assertTrue(deactivationSuccess, "Deactivation of energy plan failed");
    }

    @Test
    public void getRemainingAmountForCustomerWithoutPlanTest() {
        String customerId = CsvUtils.getNextCustomer();

        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("customers/energy-plan-remaining-amount/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(false));
    }
}
