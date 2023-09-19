package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.Payment;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;


public class EnergyPlanPaymentHistoryTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    @BeforeAll
    public static void setup() throws IOException {
        PropertiesReader propReader = PropertiesReader.getReader();
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();
        RestAssured.basePath = "/customers";
    }

    @AfterAll
    public static void teardown() {
        RestAssured.reset();
    }

    /**
     * [GET] customers/payment-history/{customer-id}
     *
     * get payment history of customer without energy plan
     *
     * Expected: Should return 404 error
     */
    @Test
    public void getPaymentHistoryOfInvalidCustomerTest() {
        String customerId = "1692455325-e43b-4608-8d8a-29458e6dbe69";

        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("/payment-history/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("success", equalTo(false));
    }

    /**
     * [GET] customers/payment-history/{customer-id}
     *
     * Assign an energy plan to customer, makes a payment and verifies
     * the payment history.
     *
     * Expected: Payment made should be retrieved in payment history
     */
    @Test
    public void getPaymentHistoryOfValidCustomerTest() throws IOException {
        String customerId = "1692271624-cb6a-46ff-acec-436240c1dc4d";
        int energyPlanId = 260;
        int settlementAmount = 1000;

        try {
            // Activate a plan for customer
            ActivatePlanForCustomer activationReq = new ActivatePlanForCustomer(energyPlanId, customerId);
            boolean planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, activationReq);
            assertTrue(planActivated, "Energy plan activatoin failed for customer: " + customerId);

            // Make payment
            Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
            payment.setOfferId(energyPlanId);
            payment.setCustomerId(customerId);
            payment.setSettlementAmount(settlementAmount);
            boolean paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(RestAssured.baseURI, RestAssured.port, payment);
            assertTrue(paymentSuccess, "Payment failed");

            // Payment should reflect in history
            Payment paid = given()
                .pathParam("customer-id", customerId)
            .when()
                .get("/payment-history/{customer-id}")
            .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
            .extract().jsonPath().getObject("response[0]", Payment.class);

            assertEquals(payment, paid);
        } finally {
            EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        }
    }
}
