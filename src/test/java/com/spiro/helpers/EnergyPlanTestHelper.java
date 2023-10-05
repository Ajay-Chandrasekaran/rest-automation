package com.spiro.helpers;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;

import org.apache.http.HttpStatus;

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

    public static EnergyPlanResponse1 createEnergyPlan(String host, int port, EnergyPlan plan) {
    
        String URL = host + ":" + port + "/energy-plans";
      return given()
            .header("Content-Type", ContentType.JSON)
            .body(plan)
        .when()
            .post(URL)
            .then().extract().as(EnergyPlanResponse1.class);
    }

    public static Response activateEnergyPlanForCustomer(String host, int port, ActivatePlanForCustomer req) {
        
        String URL = host + ":" + port + "/customers/energy-plans";
      
        return given()
            .contentType(ContentType.JSON)
            .body(req)
        .when()
            .put(URL)
        .then()
        .extract().response();

    }

    public static Response deactivateEnergyPlanForCustomer(String host, int port, String customerId) {
     
        String URL = host + ":" + port + "/customers/{customer-id}/energy-plans";

       return given()
            .pathParam("customer-id", customerId)
        .when()
            .patch(URL)
        .then()
        .extract().response();

       
    }
    
    /**
     * 
     * @param host
     * @param port
     * @param payment
     * @return
     */
    public static Response createPaymentHistory(String host, int port, Payment payment) {
       
        String URL = host + ":" + port + "/customers/payments/history";
       
        ArrayList<Payment> paymentList = new ArrayList<>();
        paymentList.add(payment);
     
      
      return given()
            .contentType(ContentType.JSON)
            .body(paymentList)
        .when()
            .post(URL)
        .then()
            .extract().response();

    }

    public static void createSwapHistory(String host,int port,SwapsHistory swap) {

      String URL = host + ":" + port + "/customers/swaps/history";
      
      RestAssured.given()
             .accept(ContentType.JSON)
             .contentType("application/json")
             .body(swap)
        .when()
             .post(URL)
        .then()
             .extract().as(SwapHistoryResponse.class);


    }

    public static float getRemainingBalance(String host,int port,String customerId) {

        String URL=host+ ":" + port + "/customers/{customer-id}/energy-plan-remaining-amount/";
        float remainingBalance =
             given().accept(ContentType.JSON)
                    .pathParam("customer-id", customerId)
             .when()
                    .get(URL)
             .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .jsonPath()
                    .getFloat("response.remainingDueAmount");
            return remainingBalance;

    }

    public static CustomerByIdEnergyPlanResponse getCutomerById(String host,int port,String customerId) {
        String URL=host+ ":" + port + "/customers/{customer-id}/energy-plans";

         CustomerByIdEnergyPlanResponse customerResponse = given()
                .pathParam("customer-id", customerId)
            .when()
                .get(URL)
            .then()
                .extract()
                .as(CustomerByIdEnergyPlanResponse.class);
    
         return customerResponse;
    }
    
    public static Response getCustomerPaymentHistory(String host,int port,String customerId) {
        String URL=host+ ":" + port + "/customers/{customerId}/payment-history/";
        
       return given()
            .pathParam("customerId",customerId)
        .when()
            .get(URL)
        .then()
            .extract().response();
    }
}


