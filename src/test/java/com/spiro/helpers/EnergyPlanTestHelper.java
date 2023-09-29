package com.spiro.helpers;

import static io.restassured.RestAssured.given;

import java.io.IOException;

import org.apache.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.Payment;
import com.spiro.entities.PaymentHistoryList;
import com.spiro.utils.CsvUtils;
import com.spiro.utils.PropertiesReader;


public class EnergyPlanTestHelper {

    // TODO: Move this code out of static block
    static {
        PropertiesReader prop = null;

        try {
            prop = PropertiesReader.getReader();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        RestAssured.baseURI = prop.getHost();
        RestAssured.port = prop.getPort();

        try {
            CsvUtils.loadCustomers(prop.getEnv());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static int createEnergyPlan(EnergyPlan plan) {
        int planId = -1;
        String URL = "/energy-plans";
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

    public static boolean activateEnergyPlanForCustomer(ActivatePlanForCustomer req) {
        boolean success = false;
        String URL = "/customers/energy-plans";

        success = given()
            .contentType(ContentType.JSON)
            .body(req)
        .when()
            .put(URL)
        .then()
        .extract().jsonPath().getBoolean("success");

        return success;
    }

    public static boolean deactivateEnergyPlanForCustomer(String customerId) {
        boolean success = false;
        String URL = "/customers/{customer-id}/energy-plans";

        success = given()
            .pathParam("customer-id", customerId)
        .when()
            .patch(URL)
        .then()
        .extract().jsonPath().getBoolean("success");

        return success;
    }

    public static boolean createPaymentHistory(Payment payment) {
        boolean success = false;
        String URL = "/customers/payments/history";
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
