package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.Payment;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;


public class EnergyPlanPaymentHistoryTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

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
            .get("/customers/payment-history/{customer-id}")
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
    // @Test
    public void getPaymentHistoryOfValidCustomerTest() throws IOException {
        String customerId = "1683292260-0bf8-4bdf-aad0-5c4fc62cb619";
        int energyPlanId = 260;
        int settlementAmount = 1000;

        try {
            // Activate a plan for customer
            ActivatePlanForCustomer activationReq = new ActivatePlanForCustomer(energyPlanId, customerId);
            boolean planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(activationReq);
            assertTrue(planActivated, "Energy plan activatoin failed for customer: " + customerId);

            // Make payment
            Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
            payment.setOfferId(energyPlanId);
            payment.setCustomerId(customerId);
            payment.setSettlementAmount(settlementAmount);
            boolean paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(payment);
            assertTrue(paymentSuccess, "Payment failed");

            // Payment should reflect in history
            Payment paid = given()
                .pathParam("customer-id", customerId)
            .when()
                .get("/customers/payment-history/{customer-id}")
            .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
            .extract().jsonPath().getObject("response[0]", Payment.class);

            assertEquals(payment, paid);
        } finally {
            EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(customerId);
        }
    }
}
