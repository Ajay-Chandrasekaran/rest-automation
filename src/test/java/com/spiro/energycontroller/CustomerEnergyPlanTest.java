package com.spiro.energycontroller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiro.utils.ObjectAndJsonUtils;
import com.spiro.utils.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class CustomerEnergyPlanTest {
    private static final String RESOURCEPATH = "src/test/resources/customerenergyplan/";
    private static final String SCHEMAPATH = "src/test/resources/jsonschemas";

    @BeforeAll
    public static void setup() throws IOException {
        PropertiesReader propReader = PropertiesReader.getReader();
        propReader.useDevEnv();
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();
        RestAssured.basePath = "/customers";

        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
            new Jackson2ObjectMapperFactory() {
                @Override
                public ObjectMapper create(Type aClass, String s) {
                    ObjectMapper oMapper = new ObjectMapper();
                    oMapper.findAndRegisterModules();
                    oMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    return oMapper;
                }
            }
        ));
    }

    @AfterAll
    public static void teardown() {
        RestAssured.reset();
    }

    @Test
    public void getCustomerEnergyPlanTest() throws JsonMappingException, JsonProcessingException, FileNotFoundException {

        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.station.size()", greaterThan(0))
            .body("response[0]", matchesJsonSchema(new FileInputStream(SCHEMAPATH + "/customer-energyplan-schema.json")));
    }

    @Test
    public void putCustomeEnergyPlanTest() throws IOException {

        JsonNode reqBody = ObjectAndJsonUtils.getJsonNodeFromFile(RESOURCEPATH + "putCustomerEnergyPlan.json");

        given()
            .contentType(ContentType.JSON)
            .body(reqBody)
        .when()
            .put("/energy-plans")
        .then()
            .body("message", equalTo("Customer has already availed one plan or existing plan not cleared."));
    }

    @Test
    public void getCustomerByIdTest() {
        given()
            .pathParam("customer-id", "12345")
            .contentType(ContentType.JSON)
        .when()
            .get("/{customer-id}/energy-plans")
        .then()
            .statusCode(HttpStatus.SC_OK);
    }

    // @Test
    public void postSwapHistory() throws IOException {
        JsonNode reqBody = ObjectAndJsonUtils.getJsonNodeFromFile(RESOURCEPATH + "SwapHistory.json");
        JsonNode actual = ObjectAndJsonUtils.getJsonNodeFromFile("jsonswaphistory.json");

        ExtractableResponse<Response> extract = given()
            .contentType(ContentType.JSON)
            .body(reqBody)
        .when()
            .post("/swaps/history")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("message", equalTo("Swap history created successfully for Benin customer : BJC534910013"))
        .extract();

        assertEquals(extract, actual);
    }

    @Test
    public void getCustomersRemainingAmount() {
        given()
            .pathParam("customer-id", "BJC534910013")
            .contentType(ContentType.JSON)
        .when()
            .get("/energy-plan-remaining-amount/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("message", equalTo("Fetched customer remaining due amount Of energy plan"));
    }

    @Test
    public void postCustomerSwapHistoryTest() throws IOException {
        JsonNode reqBody = ObjectAndJsonUtils.getJsonNodeFromFile(RESOURCEPATH + "customerpaymenthistory.json");

        given()
            .contentType(ContentType.JSON)
            .body(reqBody)
        .when()
            .post("/payments/history")
        .then()
            .statusCode(HttpStatus.SC_CREATED)
        .body("message[0]", equalTo("Payment history created successfully for customer"));
    }

    @Test
    public void getCustomerSwapHistoryTest() {
        given()
            .pathParam("customer-id", "BJC534910013")
            .contentType(ContentType.JSON)
        .when()
            .get("/payment-history/{customer-id}")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("message", equalTo("Fetched energy plan payment history."));
    }
}
