package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.EnergyPlanResponse1;
import com.spiro.helpers.EnergyPlanTestHelper;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GetEnergyPlanAvailedList {

  
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

         EnergyPlanResponse1 energyPlanId = EnergyPlanTestHelper.createEnergyPlan(RestAssured.baseURI, RestAssured.port, plan);

         return energyPlanId.getResponse().getId();

     }

    /*
     * 
     * Activating a energy plan to a customer based on is CustomerId
     *
     */
    @Test
    @Order(1)
    public void putActivateEnergyPlanBycustomerId1() throws IOException {

    ActivatePlanForCustomer activatePlanForCustomer = new ActivatePlanForCustomer();
    activatePlanForCustomer.setCustomerId(ObjectAndJsonUtils.UUIDgenerator());
    
    Response activateEnergyPlanForCustomer = EnergyPlanTestHelper.activateEnergyPlanForCustomer(RestAssured.baseURI, RestAssured.port,activatePlanForCustomer);
    activatePlanForCustomer.setPlanId(createEnergyPlan());  
       System.out.println(activatePlanForCustomer);
        RestAssured.given().contentType(ContentType.JSON)
                      .body(activateEnergyPlanForCustomer).when().put("/energy-plans")
                .then()
                 .statusCode(HttpStatus.SC_CREATED)
                 .body("success", equalTo(true))
                .body("message", equalTo("Customer has successfully availed energy plan."));
    }

//    @Test
//    public void putActivateEnergyPlanBycustomerId() {
//
//        Object convertJsonFiletoObject = convertJsonFiletoObject(JSONPATH + "putCustomerEnergyPlan.json");
//
//        ActivateEnergyPlan convertJsontoSpecificClassType = convertJsontoSpecificClassType(convertJsonFiletoObject,
//                ActivateEnergyPlan.class);
//        convertJsontoSpecificClassType.setCustomerId(UUIDgenerator());
//
//        RestAssured.given().contentType(ContentType.JSON).body(convertJsonFiletoObject).when().put("/energy-plans")
//                .then().statusCode(HttpStatus.SC_BAD_REQUEST)
//                .body("message", equalTo("Customer has already availed one plan or existing plan not cleared."));
//    }

//    @Test
//    @Order(2)
//    public void validatePutCustomerEnergyPlanWithParameterSize() {
//        /*
//         * ActivateEnergyPlan energyPlan = new ActivateEnergyPlan("BJC591068705",
//         * "ATHENA_PORTAL"); Test case should pass when planId is not given
//         */
//        RestAssured.given().contentType(ContentType.JSON).body(new ActivateEnergyPlan("BJC591068705", "ATHENA_PORTAL"))
//                .when().put("/energy-plans").then().statusCode(HttpStatus.SC_BAD_REQUEST)
//                .body("message", equalTo("Selected plan does not exist"));
//
//        // test case should fail when given random planId
//        RestAssured.given().contentType(ContentType.JSON)
//                .body(new ActivateEnergyPlan(75564, "B365JC68559544s105405", "ATHENA_PORTAL")).when()
//                .put("/energy-plans").then().statusCode(HttpStatus.SC_BAD_REQUEST)
//                .body("message", equalTo("Selected plan does not exist"));
//
//        // test case should pass if customerId is not present
//        ActivateEnergyPlan energyPlan2 = new ActivateEnergyPlan(7, "ATHENA_PORTAL");
//
//        RestAssured.given().contentType(ContentType.JSON).body(energyPlan2).when().put("/energy-plans").then()
//                .statusCode(HttpStatus.SC_BAD_REQUEST).body("message", equalTo("Customer id is not present."));
//    }
//
//    @Test
//    @Order(3)
//    public void validateCustomerEnergyPlanSize() {
//        List<Integer> path = given().contentType(ContentType.JSON).when().get("/energy-plans").then()
//                .statusCode(HttpStatus.SC_OK).body("message", equalTo("Customer fetched successfully.")).extract()
//                .path("response.customerId");
//
//        assertTrue(path.size() > 0);
//    }
//
//    @Test
//    @Order(4)
//    public void getCustomerEnergyPlan() throws FileNotFoundException {
//
//        Object jsonFiletoObject = convertJsonFiletoObject(JSONPATH + "putCustomerEnergyPlan.json");
//        ActivateEnergyPlan convertJsontoSpecificClassType = convertJsontoSpecificClassType(jsonFiletoObject,
//                ActivateEnergyPlan.class);
//        convertJsontoSpecificClassType.setCustomerId(UUIDgenerator());
//
//        ActivateEnergyPlan energyPlan = convertJsontoSpecificClassType(jsonFiletoObject, ActivateEnergyPlan.class);
//
//        EnergyPlanFullResponse data = given().contentType(ContentType.JSON).when().get("/energy-plans").then()
//                .statusCode(HttpStatus.SC_OK).body("message", equalTo("Customer fetched successfully.")).extract()
//                .as(EnergyPlanFullResponse.class);
//
//        data.getResponse().stream().forEach((r) -> {
//            if (energyPlan.getCustomerId().equals(r.getCustomerId())) {
//                System.out.println(r.getEnergyPlanInfo().getId());
//                if (r.getEnergyPlanInfo().getId() == (energyPlan.getPalnId())) {
//                    assertTrue(true);
//                }
//                if (r.getEnergyPlanInfo().getStatus() == 1) {
//                    assertTrue(true);
//                }
//            }
//        });
//
//        ArrayList<EnergyPlanResponse> arrayList = new ArrayList<EnergyPlanResponse>();
//
//        data.getResponse().stream().forEach((r) -> {
//            if (r.getEnergyPlanInfo().getStatus() == 1) {
//                arrayList.add(r);
//            }
//        });
//
//        if (arrayList.size() == 0) {
//            assertTrue(true);
//        }
//    }
//
//    // @Test
//    @Order(5)
//    public void postSwapHistory() {
//
//        Object jsonFiletoObject = convertJsonFiletoObject(JSONPATH + "postcustomerswaphistory");
//
//        Object jsonExpected = convertJsonFiletoObject(
//                "src/test/resources/customerenergyresources/expectedswaphistoryresult");
//
//        SwapHistoryPojo actual = given().contentType(ContentType.JSON).body(jsonFiletoObject).when()
//                .post("/swaps/history").then().body("status", equalTo(HttpStatus.SC_CREATED))
//                .body("message",
//                        equalTo("Swap history created successfully for Benin customer : 454858478747442744745"))
//                .extract().jsonPath().getObject("response", SwapHistoryPojo.class);
//
//        System.out.println(actual);
//
//        SwapHistoryPojo expected = convertJsontoSpecificClassType(jsonExpected, SwapHistoryPojo.class);
//        System.out.println(expected);
//
//        assertEquals(actual, expected);
//    }
//}

}
