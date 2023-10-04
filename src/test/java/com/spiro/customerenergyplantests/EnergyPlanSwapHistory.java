package com.spiro.customerenergyplantests;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
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
     * Create a energy plan and get planId and return it to energyPlanSwapCount
     */
    public int createEnergyPlan() throws IOException {
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusMonths(1).toString();
        Integer swapCount = 5;

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

        ActivatePlanForCustomer customer = new ActivatePlanForCustomer();
        customer.setCustomerId(customerId);
        customer.setPlanId(id);

        EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customer);

        CustomerByIdEnergyPlanResponse custEnergy = RestAssured.given().accept(ContentType.JSON)
                .pathParam("customer-id", customerId).when().get("/customers/{customer-id}/energy-plans").then()
                .extract().as(CustomerByIdEnergyPlanResponse.class);

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

        CustomerByIdEnergyPlanResponse allocateEnergyPlanToCustomer = allocateEnergyPlanToCustomer(createEnergyPlan);

        int id = allocateEnergyPlanToCustomer.getResponse().getEnergyPlanInfo().getId();
        EnergyPlanResponse1 planInfo = RestAssured.given().accept(ContentType.JSON).pathParam("id", id).when()
                .get("/energy-plans/{id}").then().extract().as(EnergyPlanResponse1.class);
        int swapCount = planInfo.getResponse().getSwapCount();
        String customerId = allocateEnergyPlanToCustomer.getResponse().getCustomerId();
        System.out.println(customerId);
        SwapHistory(swapCount, customerId);
        PaymentHistory(customerId, id);
    }

    public void SwapHistory(Integer swapCount, String customerId) throws IOException {

        SwapsHistory createObjectFromJsonFile = ObjectAndJsonUtils
                .createObjectFromJsonFile(RESOURCEPATH + "jsonswaphistory.json", SwapsHistory.class);

        for (int i = 0; i < swapCount; i++) {
            createObjectFromJsonFile.setCustomerId(customerId);
            EnergyPlanTestHelper.createSwapHistory(RestAssured.baseURI, RestAssured.port, createObjectFromJsonFile);

        }
    }

    public void PaymentHistory(String customerId, int planId) throws IOException {

        Payment createObjectFromJsonFile = ObjectAndJsonUtils
                .createObjectFromJsonFile(RESOURCEPATH + "customerpaymenthistory.json", Payment.class);
        createObjectFromJsonFile.setCustomerId(customerId);
        createObjectFromJsonFile.setOfferId(planId);
        createObjectFromJsonFile.setEmiDate(LocalDate.now().toString());
        float totalDays = 26;
        int planAmount = 2600;
        float offerEmi = planAmount / totalDays;
        createObjectFromJsonFile.setOfferEmi(offerEmi);
//        createObjectFromJsonFile.setSettlementAmount(offerEmi);

        for (int i = 0; i < 26; i++) {
            EnergyPlanTestHelper.createPaymentHistory(RestAssured.baseURI, RestAssured.port, createObjectFromJsonFile);

        }

        float remainingBalance = EnergyPlanTestHelper.getRemainingBalance(RestAssured.baseURI, RestAssured.port,
                customerId);

        if (remainingBalance == 0) {
            deactivateCustomerEnergyPlan(customerId);
        } else {
            deactiveCustomerEnergyPlanFail(customerId);
        }
    }

    public void deactivateCustomerEnergyPlan(String customerId) {

        boolean deactivateEnergyPlanForCustomer = EnergyPlanTestHelper
                .deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        System.out.println("passed");
        Assertions.assertTrue(deactivateEnergyPlanForCustomer);
    }

    public void deactiveCustomerEnergyPlanFail(String customerId) {
        boolean deactivateEnergyPlanForCustomer = EnergyPlanTestHelper
                .deactivateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port, customerId);
        System.out.println("failed");
        Assertions.assertFalse(deactivateEnergyPlanForCustomer);
    }

    @Test
    @Order(2)
    public void swapIneligibleAfterDate() throws IOException, ParseException {

        int createEnergyPlan = createEnergyPlan();

        CustomerByIdEnergyPlanResponse allocateEnergyPlanToCustomer = allocateEnergyPlanToCustomer(createEnergyPlan);

        int id = allocateEnergyPlanToCustomer.getResponse().getEnergyPlanInfo().getId();
        EnergyPlanResponse1 planInfo = RestAssured.given().accept(ContentType.JSON).pathParam("id", id).when()
                .get("/energy-plans/{id}").then().extract().as(EnergyPlanResponse1.class);

        String updatedDate = allocateEnergyPlanToCustomer.getResponse().getLastUpdatedDate();

        int swapCount = planInfo.getResponse().getSwapCount();
        String customerId = allocateEnergyPlanToCustomer.getResponse().getCustomerId();
        SwapHistory(swapCount, customerId, updatedDate);
        PaymentHistory(customerId, id);
    }

    public void SwapHistory(Integer swapCount, String customerId, String date) throws IOException, ParseException {

        SwapsHistory createObjectFromJsonFile = ObjectAndJsonUtils
                .createObjectFromJsonFile(RESOURCEPATH + "jsonswaphistory.json", SwapsHistory.class);

        CustomerByIdEnergyPlanResponse customerResponse = EnergyPlanTestHelper.getCutomerById(RestAssured.baseURI,
                RestAssured.port, customerId);

        String endDate = customerResponse.getResponse().getEndDate();
        String updatedDate = customerResponse.getResponse().getLastUpdatedDate();

        String updateDate1 = dateTime(updatedDate);

        for (int i = 0; i < swapCount; i++) {
            if (endDate.compareTo(updateDate1) > 0)

                createObjectFromJsonFile.setCustomerId(customerId);
            EnergyPlanTestHelper.createSwapHistory(RestAssured.baseURI, RestAssured.port, createObjectFromJsonFile);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date3 = outputFormat.parse(updateDate1);
            Calendar instance = Calendar.getInstance();
            instance.setTime(date3);
            instance.add(Calendar.DAY_OF_MONTH, 10);
            Date time = instance.getTime();
            String format = outputFormat.format(time);
            updateDate1 = format;
        }
    }

    public String dateTime(String date) throws ParseException {

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date inputDate = outputFormat.parse(date);

        String formattedDate = outputFormat.format(inputDate);

        return formattedDate;
    }

}
