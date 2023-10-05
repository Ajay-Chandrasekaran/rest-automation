package com.spiro.helpers;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.Payment;
import com.spiro.entities.PaymentHistoryList;
import com.spiro.entities.SwapHistory;


public class EnergyPlanTestHelper {

    public static Response createEnergyPlan(EnergyPlan plan) {
        String URL = "/energy-plans";

        return given()
            .header("Content-Type", ContentType.JSON)
            .body(plan)
        .when()
            .post(URL)
        .then()
            .statusCode(HttpStatus.SC_CREATED)
        .extract().response();
    }

    public static Response activateEnergyPlanForCustomer(ActivatePlanForCustomer req) {
        String URL = "/customers/energy-plans";

        return given()
            .contentType(ContentType.JSON)
            .body(req)
        .when()
            .put(URL)
        .then().extract().response();
    }

    public static Response deactivateEnergyPlanForCustomer(String customerId) {
        String URL = "/customers/{customer-id}/energy-plans";

        return given()
            .pathParam("customer-id", customerId)
        .when()
            .patch(URL)
        .then().extract().response();
    }

    public static Response createPaymentHistory(Payment payment) {
        String URL = "/customers/payments/history";

        PaymentHistoryList history = new PaymentHistoryList();
        history.getHistory().add(payment);

        return given()
            .body(history.getHistory())
            .contentType(ContentType.JSON)
        .when()
            .post(URL)
        .then().extract().response();
    }

    public static Response createSwapHistory(SwapHistory swap) {
        String URL = "/customers/swaps/history";

        return given()
            .accept(ContentType.JSON)
            .contentType("application/json")
            .body(swap)
        .when()
            .post(URL)
        .then().extract().response();
    }

    public static Response getRemainingBalance(String host,int port,String customerId) {
        String URL = "/customers/{customer-id}/energy-plan-remaining-amount/";

        return given()
            .accept(ContentType.JSON)
            .pathParam("customer-id", customerId)
        .when()
            .get(URL)
        .then().extract().response();
    }

    public static Response getCutomerById(String customerId) {
        String URL = "/customers/{customer-id}/energy-plans";

        return given()
            .pathParam("customer-id", customerId)
        .when()
            .get(URL)
        .then().extract().response();
    }

    public static void init() {}
}
