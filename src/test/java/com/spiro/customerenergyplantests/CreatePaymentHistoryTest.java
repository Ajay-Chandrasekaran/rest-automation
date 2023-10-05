package com.spiro.customerenergyplantests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.Payment;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreatePaymentHistoryTest {

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
    public void createPaymentForValidCustomerTest() throws IOException {
        String customerId = ObjectAndJsonUtils.UUIDgenerator();
        int energyPlanId = 1010;
        int settlementAmount = 5000;

        try {
            // Activate a plan for customer
            ActivatePlanForCustomer activationReq = new ActivatePlanForCustomer(energyPlanId, customerId);
            Response planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, activationReq);
            assertTrue(planActivated.jsonPath().getBoolean("success"), "Energy plan activatoin failed for customer: " + customerId);

            // Make payment
            Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
            payment.setOfferId(energyPlanId);
            payment.setCustomerId(customerId);
            payment.setSettlementAmount(settlementAmount);
            Response paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(RestAssured.baseURI, RestAssured.port, payment);          
            assertTrue(paymentSuccess.jsonPath().getBoolean("[0].success"), "Payment passed");
        } finally {
            EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        }
    }

    @Test
    public void createPaymentForCustomerWithoutPlanTest() throws IOException {
        String customerId = ObjectAndJsonUtils.UUIDgenerator();
        int energyPlanId = 260;
        int settlementAmount = 1000;

        // Make payment
        Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
        payment.setOfferId(energyPlanId);
        payment.setCustomerId(customerId);
        payment.setSettlementAmount(settlementAmount);

        Response paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(RestAssured.baseURI, RestAssured.port, payment);

        assertFalse(paymentSuccess.jsonPath().getBoolean("success"), "Payment history created for customer without a energy plan");
    }
}
