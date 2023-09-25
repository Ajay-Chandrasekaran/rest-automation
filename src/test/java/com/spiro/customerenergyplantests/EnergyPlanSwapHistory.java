package com.spiro.customerenergyplantests;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.CustomerByIdEnergyPlanResponse;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.EnergyPlanResponse1;
import com.spiro.entities.Payment;
import com.spiro.entities.SwapsHistory;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnergyPlanSwapHistory {

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
     * Create a energy plan and get planId
     */
        public int createEnergyPlan() throws IOException {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusMonths(1).toString();
        Integer swapCount = 5;

        /*
         * Energy plan creation
         */
        EnergyPlan plan = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "energy-plan.json",
                EnergyPlan.class);

        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setSwapCount(swapCount);
        plan.setPlanTotalValue(2600);

        int energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, plan);

        return energyPlanId;

    }

    /*
     * allocate an energy plan to a customer and return an Entity
     */
    public CustomerByIdEnergyPlanResponse allocateEnergyPlanToCustomer(int id) throws IOException {
        UUID randomUUID = UUID.randomUUID();
        String customerId = String.valueOf(randomUUID);
        System.out.println(customerId);
        ActivatePlanForCustomer customer = new ActivatePlanForCustomer();
        customer.setCustomerId(customerId);
        customer.setPlanId(id);

        EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customer);

        CustomerByIdEnergyPlanResponse custEnergy = RestAssured.given().accept(ContentType.JSON)
                .pathParam("customer-id", customerId).when().get("/customers/{customer-id}/energy-plans").then()
                .extract().as(CustomerByIdEnergyPlanResponse.class);

        System.out.println(custEnergy.toString());
        return custEnergy;
    }

    /*
     * makes swap and update swap transaction history and validate start date and
     * end date of customers energy plan and check the EMI amount to be paid daily,
     * updated properly and is due amount is cleared to avail new energy plan if the
     * date is passed and he as settled all the amount he should not be eligible to
     * swap the remaining swap will be lapsed
     */
    @Test
    @Order(1)
    public void energyplanSwapCount() throws IOException {

        int createEnergyPlan = createEnergyPlan();
        System.out.println("energyplanid1 " + createEnergyPlan);
        CustomerByIdEnergyPlanResponse allocateEnergyPlanToCustomer = allocateEnergyPlanToCustomer(createEnergyPlan);

        int id = allocateEnergyPlanToCustomer.getResponse().getEnergyPlanInfo().getId();
        EnergyPlanResponse1 planInfo = RestAssured.given().accept(ContentType.JSON).pathParam("id", id).when()
                .get("/energy-plans/{id}").then().extract().as(EnergyPlanResponse1.class);
        int swapCount = planInfo.getResponse().getSwapCount();
        String customerId = allocateEnergyPlanToCustomer.getResponse().getCustomerId();
        SwapHistory(swapCount,customerId);
        PaymentHistory(customerId,id);
    }

    public void SwapHistory(Integer swapCount,String customerId) throws IOException {
        System.out.println(swapCount);
        SwapsHistory createObjectFromJsonFile = ObjectAndJsonUtils
                .createObjectFromJsonFile(RESOURCEPATH + "jsonswaphistory.json", SwapsHistory.class);
        System.out.println("swap "+ createObjectFromJsonFile);
        for (int i = 0; i < swapCount; i++) {
        createObjectFromJsonFile.setCustomerId(customerId);
            EnergyPlanTestHelper.createSwapHistory(RestAssured.baseURI, RestAssured.port, createObjectFromJsonFile);
            System.out.println(i);
        }
    }
    
    public void PaymentHistory(String customerId,int planId) throws IOException {
       
        Payment createObjectFromJsonFile = ObjectAndJsonUtils.createObjectFromJsonFile(RESOURCEPATH + "customerpaymenthistory.json", Payment.class);
        createObjectFromJsonFile.setCustomerId(customerId);
        createObjectFromJsonFile.setOfferId(planId);
        createObjectFromJsonFile.setEmiDate(LocalDate.now().toString());
        float totalDays=26;
        int planAmount=2600;
        float offerEmi=planAmount/totalDays;
        createObjectFromJsonFile.setOfferEmi(offerEmi);
        createObjectFromJsonFile.setSettlementAmount(offerEmi);
        
        for (int i = 0; i < 26; i++) {
            EnergyPlanTestHelper.createPaymentHistory(RestAssured.baseURI, RestAssured.port, createObjectFromJsonFile);
        }        
        
      Float remainingBalance = EnergyPlanTestHelper.getRemainingBalance(RestAssured.baseURI, RestAssured.port, customerId);
          
      if(remainingBalance==0) {
          deactivateCustomerEnergyPlan(customerId);
      }
        
    }
   
    public void deactivateCustomerEnergyPlan(String customerId) {
       
        boolean deactivateEnergyPlanForCustomer = EnergyPlanTestHelper.deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        Assertions.assertTrue(deactivateEnergyPlanForCustomer);
    }
    
//    public void 
    

}
