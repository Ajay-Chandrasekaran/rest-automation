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

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.Payment;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class EnergyPlanPaymentHistoryTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    @BeforeAll
    public static void setup() throws IOException {
        PropertiesReader propReader = PropertiesReader.getReader();
        propReader.setEnv("dev");
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();
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

        given().pathParam("customer-id", customerId).when().get("/customers/{customer-id}/payment-history/").then()
                .statusCode(HttpStatus.SC_BAD_REQUEST).body("success", equalTo(false));
    }

    /**
     * [GET] customers/payment-history/{customer-id}
     *
     * Assign an energy plan to customer, makes a payment and verifies the payment
     * history.
     *
     * Expected: Payment made should be retrieved in payment history
     */
  //  @Test
    public void getPaymentHistoryOfValidCustomerTest() throws IOException {
        String customerId = "1690361168-0000-4fcc-9335-3f2207507c64";
        int energyPlanId = 1010;
        int settlementAmount = 5000;

        try {
            // Activate a plan for customer
            ActivatePlanForCustomer activationReq = new ActivatePlanForCustomer(energyPlanId, customerId);
            Response planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI,
                    RestAssured.port, activationReq);
            assertTrue(planActivated.jsonPath().getBoolean("success"),
                    "Energy plan activatoin failed for customer: " + customerId);

            // Make payment
            Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json",
                    Payment.class);
            payment.setOfferId(energyPlanId);
            payment.setCustomerId(customerId);
            payment.setSettlementAmount(settlementAmount);
            Response paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(RestAssured.baseURI, RestAssured.port,
                    payment);
            System.out.println(paymentSuccess.asPrettyString());
            assertTrue(paymentSuccess.jsonPath().getBoolean("[0].success"), "Payment failed");

            
              // Payment should reflect in history
              
              Payment paid = given() .pathParam("customerId", customerId) .when()
              .get("/customers/{customerId}/payment-history/") .then()
              .statusCode(HttpStatus.SC_OK) .body("success", equalTo(true)) .extract()
              .jsonPath() .getObject("response", Payment.class); System.out.println(paid);
               assertEquals(payment, paid);
             

            // Response customerPaymentHistory =
            // EnergyPlanTestHelper.getCustomerPaymentHistory(RestAssured.baseURI,
            // RestAssured.port, customerId);
            // System.out.println(customerPaymentHistory.asPrettyString());
        } finally {
            EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        }
    }
}
