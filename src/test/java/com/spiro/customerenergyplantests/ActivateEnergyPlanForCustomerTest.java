package com.spiro.customerenergyplantests;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.CsvUtils;
import com.spiro.utils.ObjectAndJsonUtils;


public class ActivateEnergyPlanForCustomerTest {

    private final String RESOURCEPATH = "src/test/resources/customerenergyplantests/";

    @BeforeClass
    public static void setup() throws IOException {
        EnergyPlanTestHelper.init();
    }

    /*
     * [PUT] /customers/energy-plans
     *
     * Create a new plan and activate it for a customer
     *
     * Expected: Energy plan should be assigned
     */
    @Test
    public void activateEnergyPlanForCustomerTest() throws IOException {
        String customerId = CsvUtils.getNextCustomer();
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        // Energy plan creation
        EnergyPlan plan = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setSwapCount(0);
        plan.setPlanTotalValue(0);

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(plan);
        assertNotEquals(-1, energyPlanId, "Energy plan creation failed");

        // test plan activation
        ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(energyPlanId, customerId);
        given()
            .contentType(ContentType.JSON)
            .body(activateReq)
        .when()
            .put("/customers/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_CREATED)
            .body("success", equalTo(true));

        if (!EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(customerId)) {
            System.err.println("Energy plan(" + energyPlanId + ") deactivation for customer : " + customerId + " Failed");
        }
    }

    /*
     * [PUT] /customers/energy-plans
     *
     * Activate an energyplan for invliad (non existing customer)
     *
     * Expected: Activation should fail
     */
    @Test
    public void activatePlanForInvalidCustomerTest() throws IOException {
        String customerId = CsvUtils.getNextCustomer();
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        // Energy plan creation
        EnergyPlan plan = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setSwapCount(0);
        plan.setPlanTotalValue(0);

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(plan);
        assertNotEquals(-1, energyPlanId, "Energy plan creation failed");

        // test plan activation
        ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(energyPlanId, customerId);
        given()
            .contentType(ContentType.JSON)
            .body(activateReq)
        .when()
            .put("/customers/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("success", equalTo(false));
    }

    /*
     * [PUT] /customers/energy-plans
     *
     * Tries to assign a plan that is not active
     *
     * Expected: Activation should fail
     */
    @Test
    public void assignNonActivePlanForCustomerTest() throws IOException {
        String customerId = CsvUtils.getNextCustomer();

        // Start and end date are in future to the status will be "yet to start" (Non active)
        String startDate = LocalDate.now().plusDays(10).toString();
        String endDate = LocalDate.now().plusDays(15).toString();

        // Energy plan creation
        EnergyPlan plan = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setSwapCount(0);
        plan.setPlanTotalValue(0);

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(plan);
        assertNotEquals(-1, energyPlanId, "Energy plan creation failed");

        // test plan activation
        ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(energyPlanId, customerId);
        given()
            .contentType(ContentType.JSON)
            .body(activateReq)
        .when()
            .put("/customers/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("success", equalTo(false))
            .body("message", equalTo("Energy Plan which has been availed is not active"));
    }

    /*
     * [PUT] /customers/energy-plans
     *
     * Create a new plan and activate it for a customer (has a plan already)
     *
     * Expected: Energy plan should be assigned
     */
    @Test
    public void activateEnergyPlanForCustomerWithPlanTest() throws IOException {
        String customerId = CsvUtils.getNextCustomer();
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        // Energy plan creation
        EnergyPlan plan = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setSwapCount(0);
        plan.setPlanTotalValue(0);

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(plan);
        assertNotEquals(-1, energyPlanId, "Energy plan creation failed");

        // test plan activation
        ActivatePlanForCustomer activateReq = new ActivatePlanForCustomer(energyPlanId, customerId);
        given()
            .contentType(ContentType.JSON)
            .body(activateReq)
        .when()
            .put("/customers/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_CREATED)
            .body("success", equalTo(true));

        activateReq.setPlanId(260);
        boolean planActivated = EnergyPlanTestHelper.activateEnergyPlanForCustomer(activateReq);
        assertFalse(planActivated, "Frist Energy plan activation failed");

        if (!EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(customerId)) {
            System.err.println("Energy plan(" + energyPlanId + ") deactivation for customer : " + customerId + " Failed");
        }
    }
}
