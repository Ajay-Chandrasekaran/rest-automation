package com.spiro.customerenergyplantests;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.io.IOException;

import org.testng.annotations.Test;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.Payment;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.CsvUtils;
import com.spiro.utils.ObjectAndJsonUtils;


public class CreatePaymentHistoryTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    @Test
    public void createPaymentForValidCustomerTest() throws IOException {
        String customerId = CsvUtils.getNextCustomer();
        int energyPlanId = 260;
        int settlementAmount = 1000;

        try {
            // Activate a plan for customer
            ActivatePlanForCustomer activationReq = new ActivatePlanForCustomer(energyPlanId, customerId);
            boolean planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(activationReq).jsonPath().getBoolean("success");
            assertTrue(planActivated, "Energy plan activatoin failed for customer: " + customerId);

            // Make payment
            Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
            payment.setOfferId(energyPlanId);
            payment.setCustomerId(customerId);
            payment.setSettlementAmount(settlementAmount);
            boolean paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(payment).jsonPath().getBoolean("[0].success");
            assertTrue(paymentSuccess, "Payment failed");
        } finally {
            EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(customerId);
        }
    }

    @Test
    public void createPaymentForCustomerWithoutPlanTest() throws IOException {
        String customerId = CsvUtils.getNextCustomer();
        int energyPlanId = 260;
        int settlementAmount = 1000;

        // Make payment
        Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
        payment.setOfferId(energyPlanId);
        payment.setCustomerId(customerId);
        payment.setSettlementAmount(settlementAmount);

        boolean paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(payment).jsonPath().getBoolean("[0].success");

        assertFalse(paymentSuccess.jsonPath().getBoolean("success"), "Payment history created for customer without a energy plan");
    }
}
