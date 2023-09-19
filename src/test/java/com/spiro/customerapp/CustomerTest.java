package com.spiro.customerapp;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.greaterThan;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import com.spiro.utils.PropertiesReader;

public class CustomerTest {
    private static final String SCHEMAPATH = "src/test/resources/jsonschemas";

    @BeforeAll
    public static void setup() throws IOException {
        PropertiesReader propReader = PropertiesReader.getReader();
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();
        RestAssured.basePath = "/v3/api/driver";
    }

    @AfterAll
    public static void teardown() {
        RestAssured.reset();
    }

    @Test
    public void testLoginSuccessful() throws IOException {
        String res = given()
            .contentType(ContentType.MULTIPART)
            .multiPart("dial_code", "+229")
            .multiPart("mobile_number", "66810049")
            .header("Authkey", "melectric")
        .when()
            .post("/login")
        .then()
            .statusCode(HttpStatus.SC_OK)
        .extract().body().asString();

        String expected = Files.readString(Path.of("src/test/resources/customerapp/LoginResponse.json"));
        try {
            JSONAssert.assertEquals(expected, res, JSONCompareMode.NON_EXTENSIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSwappingStations () throws IOException {
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("driver_id", "1684172807-7156-4fe3-b727-b418f13c4f58")
            .multiPart("lat", "6.361079")
            .multiPart("lon", "2.422579")
            .header("Authkey", "melectric")
        .when()
            .post("/swapstation/get")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.station.size()", greaterThan(0));
    }

    @Test
    public void getServiceStations () throws IOException {
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("driver_id", "1684172807-7156-4fe3-b727-b418f13c4f58")
            .multiPart("lat", "6.361079")
            .multiPart("lon", "2.422579")
            .header("Authkey", "melectric")
        .when()
            .post("/servicestation/get")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.station.size()", greaterThan(0))
            .body(matchesJsonSchema(new FileInputStream(SCHEMAPATH + "/servicestations-schema.json")));
    }
}
