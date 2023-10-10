package com.spiro.customerenergyplantests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import com.spiro.utils.CsvUtils;

public class EnergyPlanByCustomerTest {

    /**
     * [GET] customers/{{customer-id}}/energy-plans
     *
     *  getting energy plan of invalid customer should fail.
     */
    @Test
    public void invalidCustomerTest() {
        String customerId = "DUMMYCUSTOMERID";
        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("/{customer-id}/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("message", equalTo("Customer does not exist by given id."));
    }

    /**
     * [GET] customers/{{customer-id}}/energy-plans
     *
     * Get should fail for a valid customer without an Energy plan.
     */
    @Test
    public void customerWithoutEnergyPlanTest() {
        String customerId = CsvUtils.getNextCustomer();

        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("/{customer-id}/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("message", equalTo("Customer does not exist by given id."));

    }

    /**
     * [GET] customers/{{customer-id}}/energy-plans
     *
     * Should get details of valid customer's energy plan. (Customer already has availed energy plan)
     */
    @Test
    public void customerWithEnergyPlanTest() {
        String customerId = CsvUtils.getNextCustomer();

        given()
            .pathParam("customer-id", customerId)
        .when()
            .get("/{customer-id}/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.id", notNullValue())
            .body("response.energyPlanInfo.offer.countryCode", equalTo("BJ"))
            .body("response.energyPlanInfo.offer.startDate", notNullValue())
            .body("response.energyPlanInfo.offer.endDate", notNullValue());
    }
}
