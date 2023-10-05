package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.EnergyPlanResponse1;
import com.spiro.entities.Payment;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;
import com.spiro.helpers.EnergyPlanTestHelper;


public class EnergyPlanRemainingAmountTest {

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

    @Test
    public void getRemainingAmountTest() throws IOException {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();
        int totalValue = 2600;

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(0);
        reqBody.setPlanTotalValue(totalValue);

        EnergyPlanResponse1 energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, reqBody);
        Integer planId = energyPlanId.getResponse().getId();
        assertNotEquals(-1, planId, "Energy plan creation failed");

        // activate the new plan for customer
        String customerId = ObjectAndJsonUtils.UUIDgenerator();
        ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(planId, customerId);
        Response activationSuccess = EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, activateReq);
        assertTrue(activationSuccess.jsonPath().getBoolean("success"), "Energy plan activation failed");

        // validate remaining amount after assigning plan
        float remainingBalance = EnergyPlanTestHelper.getRemainingBalance(RestAssured.baseURI, RestAssured.port, customerId);
        Assertions.assertEquals(totalValue, remainingBalance);

        // ** Clean up after test **
        // pay remaining amount (pre conditoin for deactivation)
        Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
        payment.setOfferId(planId);
        payment.setCustomerId(customerId);
        payment.setSettlementAmount(totalValue);
        Response paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(RestAssured.baseURI, RestAssured.port, payment);
      
        assertTrue(paymentSuccess.jsonPath().getBoolean("[0].success"), "Payment failed");
       
        float remainingBalance1 = EnergyPlanTestHelper.getRemainingBalance(RestAssured.baseURI, RestAssured.port, customerId);
        Assertions.assertEquals(0.0f, remainingBalance1);
        
        // Deactivate energy plan
        Response deactivationSuccess = EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        assertTrue(deactivationSuccess.jsonPath().getBoolean("success"), "Deactivation of energy plan failed");
    }

    @Test
    public void getRemainingAmountForCustomerWithoutPlanTest() {
        String customerId = ObjectAndJsonUtils.UUIDgenerator();
        
        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("/customers/{customer-id}/energy-plan-remaining-amount/")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("success", equalTo(false));
    }
}
