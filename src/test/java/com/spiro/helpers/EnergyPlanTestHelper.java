package com.spiro.helpers;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.Payment;
import com.spiro.entities.PaymentHistoryList;


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
}