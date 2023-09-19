package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.Payment;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;
import com.spiro.helpers.EnergyPlanTestHelper;


public class EnergyPlanRemainingAmountTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";
    private static PropertiesReader propReader;

    @BeforeAll
    public static void setup() throws IOException {
        propReader = PropertiesReader.getReader();
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
        int totalValue = 1000;

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(0);
        reqBody.setPlanTotalValue(totalValue);

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, reqBody);
        assertNotEquals(-1, energyPlanId, "Energy plan creation failed");

        // activate the new plan for customer
        String customerId = "1683735366-1070-4fa8-bb2e-96687f0778d0";
        ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(energyPlanId, customerId);
        boolean activationSuccess = EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, activateReq);
        assertTrue(activationSuccess, "Energy plan activation failed");

        // validate remaingin amount
        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("customers/energy-plan-remaining-amount/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.remainingDueAmount", equalTo((float)totalValue));

        // ** Clean up after test **
        // pay remanining amount (pre conditoin for deactivation)
        Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
        payment.setOfferId(energyPlanId);
        payment.setCustomerId(customerId);
        boolean paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(RestAssured.baseURI, RestAssured.port, payment);
        assertTrue(paymentSuccess, "Payment failed");

        // Deactivate energy plan
        boolean deactivationSuccess = EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        assertTrue(deactivationSuccess, "Deactivation of energy plan failed");
    }

    @Test
    public void getRemainingAmountForCustomerWithoutPlan() {
        String customerId = "1692455325-e43b-4608-8d8a-29458e6dbe69";

        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("customers/energy-plan-remaining-amount/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            .body("success", equalTo(false));
    }
}
