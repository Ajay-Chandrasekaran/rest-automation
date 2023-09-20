package com.spiro.customerenergyplantests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;


public class ActivateEnergyPlanForCustomerTest {

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

    /*
     * [PUT] /customers/energy-plans
     *
     * Create a new plan and activate if for a customer
     *
     * Expected: Energy plan should be assigned
     */
    @Test
    public void activateEnergyPlanForCustomerTest() throws IOException {
        String customerId = "1690361168-39e5-4fcc-9335-3f2207507c64";

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        // Energy plan creation
        EnergyPlan plan = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setSwapCount(0);
        plan.setPlanTotalValue(0);

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, plan);
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

        if (!EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId)) {
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
        String customerId = "NO SUCH CUSTOMER";

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(5).toString();

        // Energy plan creation
        EnergyPlan plan = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setSwapCount(0);
        plan.setPlanTotalValue(0);

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, plan);
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
        String customerId = "1690361168-0000-4fcc-9335-3f2207507c64";

        // Start and end date are in future to the status will be "yet to start" (Non active)
        String startDate = LocalDate.now().plusDays(10).toString();
        String endDate = LocalDate.now().plusDays(15).toString();

        // Energy plan creation
        EnergyPlan plan = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json", EnergyPlan.class);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setSwapCount(0);
        plan.setPlanTotalValue(0);

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, plan);
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
}
