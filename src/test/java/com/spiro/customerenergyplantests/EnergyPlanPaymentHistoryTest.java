package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.Payment;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.CsvUtils;
import com.spiro.utils.ObjectAndJsonUtils;


public class EnergyPlanPaymentHistoryTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    @BeforeClass
    public static void setup() throws IOException {
        EnergyPlanTestHelper.init();
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
        String customerId = CsvUtils.getNextCustomer();

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
        String customerId = CsvUtils.getNextCustomer();
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
