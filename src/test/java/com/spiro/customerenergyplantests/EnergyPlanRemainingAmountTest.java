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
        int totalValue = 1000;

        EnergyPlan reqBody = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        reqBody.setStartDate(startDate);
        reqBody.setEndDate(endDate);
        reqBody.setSwapCount(0);
        reqBody.setPlanTotalValue(totalValue);

        EnergyPlanResponse1 energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, reqBody);
        Integer planId = energyPlanId.getResponse().getId();
        System.out.println(planId);
        assertNotEquals(-1, planId, "Energy plan creation failed");

        // activate the new plan for customer
        String customerId = "1683735366-1070-4fa8-bb2e-96687f0778d0";
        ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(planId, customerId);
        Response activationSuccess = EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, activateReq);
        assertTrue(activationSuccess.jsonPath().getBoolean("success"), "Energy plan activation failed");

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
        Response paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(RestAssured.baseURI, RestAssured.port, payment);
        assertTrue(paymentSuccess.jsonPath().getBoolean("success"), "Payment failed");

        // validating remaining amout after complete settlement
        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("customers/energy-plan-remaining-amount/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.remainingDueAmount", equalTo((float)0));

        // Deactivate energy plan
        Response deactivationSuccess = EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        assertTrue(deactivationSuccess.jsonPath().getBoolean("success"), "Deactivation of energy plan failed");
    }

    @Test
    public void getRemainingAmountForCustomerWithoutPlanTest() {
        String customerId = "1687243176-fd50-4f55-a231-84790d45fb28";

        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("customers/energy-plan-remaining-amount/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(false));
    }
}
