package com.spiro.helpers;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.CustomerByIdEnergyPlanResponse;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.EnergyPlanResponse1;
import com.spiro.entities.Payment;
import com.spiro.entities.PaymentHistoryList;
import com.spiro.entities.SwapHistoryResponse;
import com.spiro.entities.SwapsHistory;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


public class EnergyPlanTestHelper {   

    private static final Logger logger = LogManager.getLogger();

    public static Response createEnergyPlan(EnergyPlan plan) {
        String URL = "/energy-plans";
        logger.info("Creating energy plan - [POST] {}", URL);

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
        logger.info("Activating energy plan for customer - [PUT] {}", URL);
        
        return given()
            .contentType(ContentType.JSON)
            .body(req)
        .when()
            .put(URL)
        .then().extract().response();
    }

    public static Response deactivateEnergyPlanForCustomer(String customerId) {
        String URL = "/customers/{customer-id}/energy-plans";
        logger.info("Deactivating energy plan for customer - [PATCH] {}", URL.replace("{customer-id}", customerId));

        return given()
            .pathParam("customer-id", customerId)
        .when()
            .patch(URL)
        .then().extract().response();
    }

    public static Response createPaymentHistory(Payment payment) {
        String URL = "/customers/payments/history";
        logger.info("Creating payment history - [POST] {}", URL);

        PaymentHistoryList history = new PaymentHistoryList();
        history.getHistory().add(payment);

        return given()
            .body(history.getHistory())
            .contentType(ContentType.JSON)
            .body(paymentList)
        .when()
            .post(URL)
        .then().extract().response();
    }

    public static Response createSwapHistory(SwapHistory swap) {
        String URL = "/customers/swaps/history";
        logger.info("Creating swap history - [POST] {}", URL);

        return given()
            .accept(ContentType.JSON)
            .contentType("application/json")
            .body(swap)
        .when()
            .post(URL)
        .then().extract().response();
    }

    public static Response getRemainingBalance(String customerId) {
        String URL = "/customers/{customer-id}/energy-plan-remaining-amount/";
        logger.info("Geting remaining balance - [POST] {}", URL.replace("{customer-id}", customerId));

        return given()
            .accept(ContentType.JSON)
            .pathParam("customer-id", customerId)
        .when()
            .get(URL)
        .then().extract().response();
    }

    public static Response getEnergyPlanOfCutomerById(String customerId) {
        String URL = "/customers/{customer-id}/energy-plans";
        logger.info("Getting energy plan of customer by Id - [GET] {}", URL.replace("{customer-id}", customerId));

        return given()
            .pathParam("customer-id", customerId)
        .when()
            .get(URL)
        .then().extract().response();
    }
}
