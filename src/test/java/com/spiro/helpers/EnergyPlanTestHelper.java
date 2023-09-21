package com.spiro.helpers;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import com.spiro.entities.ActivatePlanForCustomer;
import com.spiro.entities.EnergyPlan;
import com.spiro.entities.Payment;
import com.spiro.utils.PropertiesReader;


public class EnergyPlanTestHelper {

    private static EnergyPlan defaultPlan;

    static {
        PropertiesReader reader = null;
        try {
            reader = PropertiesReader.getReader();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        RestAssured.baseURI = reader.getHost();
        RestAssured.port = reader.getPort();
    }

    /**
     * Send a request to create an energy plan.
     *
     * <p> [POST] /energy-plans </p>
     *
     * @param energyPlan The body to send.
     * @return The response from create energy plan endpoint.
     */
    public static Response createEnergyPlan(EnergyPlan energyPlan) {
        String URL = "/energy-plans";

        return given()
            .header("Content-Type", ContentType.JSON)
            .body(energyPlan)
        .when()
            .post(URL)
        .then().extract().response();
    }

    /**
     * Send a request to create an energy plan with default values.
     *
     * <p> [POST] /energy-plans </p>
     *
     * <p>
     * The created energy plan has a duration of one week from current date, total
     * plan value is 5000, and daily plan value of 193.00 for the Togo.
     * </p>
     *
     * @param energyPlan The body to send.
     * @return The response from create energy plan endpoint.
     */
    public static Response createDefaultEnergyPlan() {
        final String STARTDATE = LocalDate.now().toString();
        final String ENDDATE = LocalDate.now().plusDays(5).toString();
        final int TOTALVALUE = 5000;
        final int DAILYVALUE = 193;
        final String LOCID = "1686137320-5524-4314-bc0f-9ba65f088a58"; // TODO: is it Togo ?
        final String DIALCODE = "229";
        final String CREATEDBY = "ATHENA_PORTAL";

        final String URL = "/energy-plans";

        if (defaultPlan == null) {
            defaultPlan = new EnergyPlan();
            defaultPlan.setCreatedBy(CREATEDBY);
            defaultPlan.setPlanTotalValue(TOTALVALUE);
            defaultPlan.setDailyPlanValue(DAILYVALUE);
            defaultPlan.setStartDate(STARTDATE);
            defaultPlan.setEndDate(ENDDATE);
            defaultPlan.setLocationId(LOCID);
            defaultPlan.setDialCode(DIALCODE);
            defaultPlan.setPlanName("AUTO_TG_ENERGY_PLAN");
            defaultPlan.setPlanCode("AUTO_TG_ENERGY_PLAN");
        }

        return given()
            .header("Content-Type", ContentType.JSON)
            .body(defaultPlan)
        .when()
            .post(URL)
        .then()
            .statusCode(HttpStatus.SC_CREATED)
        .extract().response();
    }

    /**
     * Makes request to activate an energy plan for a customer.
     *
     * <p> [PUT] /customers/energy-plans </p>
     *
     * @param planId The plan to acitve for customer.
     * @param customerId The customer to whom the plan should be assigned.
     * @return The response of activate plan for customer endpoint.
     */
    public static Response activateEnergyPlanForCustomer(int planId, String customerId) {
        final String URL = "/customers/energy-plans";
        ActivatePlanForCustomer req = new ActivatePlanForCustomer(planId, customerId);

        return given()
            .contentType(ContentType.JSON)
            .body(req)
        .when()
            .put(URL)
        .then().extract().response();
    }

    /**
     * Makes request to deactivate customer's current energy plan.
     *
     * <p> [PATCH] /customers/{customerId}/energy-plans </p>
     *
     * @param customerId The customer whose plan is to be deactivate.
     * @return The response from the energy plan deactivation endpoint.
     */
    public static Response deactivateEnergyPlanForCustomer(String customerId) {
        final String URL = "/customers/{customer-id}/energy-plans";

        return given()
            .pathParam("customer-id", customerId)
        .when()
            .patch(URL)
        .then().extract().response();
    }

    /**
     * Makes a request to create a payment history
     *
     * <p> [POST] /customers/payments/history </p>
     *
     * @param payment The payment to be made.
     * @return The response of create payment history endpoint.
     */
    public static Response createPaymentHistory(Payment payment) {
        final String URL = "/customers/payments/history";
        List<Payment> req = new ArrayList<>();
        req.add(payment);

        return given()
            .body(payment)
            .contentType(ContentType.JSON)
        .when()
            .post(URL)
        .then().extract().response();
    }
}
