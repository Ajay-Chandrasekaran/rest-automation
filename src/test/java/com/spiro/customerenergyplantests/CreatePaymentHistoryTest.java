package com.spiro.customerenergyplantests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.Payment;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;

public class CreatePaymentHistoryTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    @Test
    public void createPaymentForValidCustomerTest() throws IOException {
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
        } finally {
            EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(customerId);
        }
    }

    @Test
    public void createPaymentForCustomerWithoutPlanTest() throws IOException {
        String customerId = "1681491471-a5b6-4ff3-948b-f2c542e64983";
        int energyPlanId = 260;
        int settlementAmount = 1000;

        // Make payment
        Payment payment = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "create-payment.json", Payment.class);
        payment.setOfferId(energyPlanId);
        payment.setCustomerId(customerId);
        payment.setSettlementAmount(settlementAmount);

        boolean paymentSuccess = EnergyPlanTestHelper.createPaymentHistory(payment);

        assertFalse(paymentSuccess, "Payment history created for customer without a energy plan");
    }
}
