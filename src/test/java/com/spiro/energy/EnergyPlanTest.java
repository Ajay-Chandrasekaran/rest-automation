package com.spiro.energy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.spiro.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class EnergyPlanTest {
  
    @BeforeAll
    public static void setup() throws IOException {
        PropertiesReader propReader = PropertiesReader.getReader();
        propReader.useDevEnv();
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();

    }

    @AfterAll
    public static void teardown() {
        RestAssured.reset();
    }
    @Test
    public void TestEnergyAPI() throws Exception {
        getEnergyCall();
        postEnergyCall();
        getEnergyByIdCall();
        patchEnergyCall();
    }
    public static void postCall(Object payload) {
        given()
            .accept(ContentType.JSON)
            .contentType("application/Json")
            .body(payload)
        .when()
            .post("/energy-plans").then().statusCode(HttpStatus.SC_CREATED);
          
    }
    
    public static void getEnergyCall() {
        given()
        .when()
            .get("/energy-plans")
         .then()
             .statusCode(HttpStatus.SC_OK)
             .body("message",equalTo("Energy Plan is fetched successfully"));

    }
   
    public static void postEnergyCall() throws FileNotFoundException {
        Gson gson = new Gson();
        String fileName = "src/test/resources/energyPlan/energyPlans.csv";
        
        Reader reader = new BufferedReader(new FileReader(fileName));
        CsvToBean<EnergyRequest> csvReader = new CsvToBeanBuilder<EnergyRequest>(reader)
                .withType(EnergyRequest.class)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .build();
        List<EnergyRequest> results = csvReader.parse();

        for (EnergyRequest e : results) {
            String requestPayload = gson.toJson(e);
            postCall(requestPayload);
        }
    }

    public static void getEnergyByIdCall() {
        int id = 43;
        given()
            .when()
                .get("/energy-plans/" + id + "")
                    .then().statusCode(HttpStatus.SC_OK)
                        .body("response.offer.offerName", equalTo("EnergyPlan_15"))
                        .body("response.offer.offerCode", equalTo("E15")).body("response.swapCount", equalTo(9))
                        .body("response.dailyPayValue", equalTo(193.0F)).body("response.offer.endDate", equalTo("2023-09-10"))
                        .body("response.offer.startDate", equalTo("2023-09-07"));
    }
    
    public static void patchEnergyCall() throws Exception {
        JSONParser parser = new JSONParser();
        int  id = 43;
        // 0->yet to start
        // 1->activate
        // 2->deactivate
        // 3->defaulter -only in in customer case
        Object parsedRequest = parser.parse(new FileReader("src/test/resources/energyPlan/patchRequest.json"));
        Object requestData = (JSONObject) parsedRequest;

        given()
            .contentType("application/Json")
            .accept(ContentType.JSON)
            .body(requestData)
         .patch("/energy-plans/" + id + "/status")
         .then()
             .statusCode(HttpStatus.SC_OK)
             .body("success", equalTo(true));

    }
    

}
