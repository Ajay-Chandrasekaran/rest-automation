package com.spiro.helpers;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.CustomerByIdEnergyPlanResponse;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.Payment;
import com.spiro.entities.PaymentHistoryList;
import com.spiro.entities.SwapHistoryResponse;
import com.spiro.entities.SwapsHistory;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


public class EnergyPlanTestHelper {

    public static int createEnergyPlan(String host, int port, EnergyPlan plan) {
        int planId = -1;
        String URL = host + ":" + port + "/energy-plans";
        planId = given()
            .header("Content-Type", ContentType.JSON)
            .body(plan)
        .when()
            .post(URL)
        .then()
            .statusCode(HttpStatus.SC_CREATED)
        .extract().path("response.id");

        return planId;
    }

    public static boolean activateEnergyPlanForCustomer(String host, int port, ActivatePlanForCustomer req) {
        boolean success = false;
        String URL = host + ":" + port + "/customers/energy-plans";

        success = given()
            .contentType(ContentType.JSON)
            .body(req)
        .when()
            .put(URL)
        .then()
        .extract().jsonPath().getBoolean("success");

        return success;
    }

    public static boolean deactivateEnergyPlanForCustomer(String host, int port, String customerId) {
        boolean success = false;
        String URL = host + ":" + port + "/customers/{customer-id}/energy-plans";

        success = given()
            .pathParam("customer-id", customerId)
        .when()
            .patch(URL)
        .then()
        .extract().jsonPath().getBoolean("success");

        return success;
    }

    public static boolean createPaymentHistory(String host, int port, Payment payment) {
        boolean success = false;

        String URL = host + ":" + port + "/customers/payments/history";
        PaymentHistoryList history = new PaymentHistoryList();
        history.getHistory().add(payment);

        Response r = given()
            .body(history.getHistory())
            .contentType(ContentType.JSON)
        .when()
            .post(URL)
        .then()
            .extract().response();

        success = r.jsonPath().getBoolean("[0].success");

        return success;
    }

    public static void createSwapHistory(String host,int port,SwapsHistory swap) {

      String URL = host + ":" + port + "/customers/swaps/history";


                RestAssured.given().accept(ContentType.JSON).contentType("application/json").body(swap)
        .when().post(URL).then().extract().as(SwapHistoryResponse.class);


    }

    public static float getRemainingBalance(String host,int port,String customerId) {

        String URL=host+ ":" + port + "/customers/{customer-id}/energy-plan-remaining-amount/";
     float remainingBalance = given().accept(ContentType.JSON).pathParam("customer-id", customerId).when().get(URL).then().extract().jsonPath().getFloat("response.remainingDueAmount");
            return remainingBalance;

    }

    public static CustomerByIdEnergyPlanResponse getCutomerById(String host,int port,String customerId) {
        String URL=host+ ":" + port + "/customers/{customer-id}/energy-plans";

         CustomerByIdEnergyPlanResponse customerResponse = given()
                .pathParam("customer-id", customerId)
            .when()
                .get(URL)
            .then()
            .extract().as(CustomerByIdEnergyPlanResponse.class);
    
         return customerResponse;
    }
}


