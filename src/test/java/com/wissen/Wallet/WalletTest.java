package com.wissen.wallet;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.wissen.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class WalletTest {

    @BeforeAll
    public static void setup() throws IOException {
        PropertiesReader propReader = PropertiesReader.getReader();
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();
    }

    @AfterAll
    public static void teardown() {
        RestAssured.reset();
    }

    @Test
    public void testGetWalletDetails() {
        given()
            .contentType(ContentType.MULTIPART)
            .multiPart("id", "1685440983-021e-4821-b08a-e4a144396409")
            .multiPart("user_type", "driver")
        .when()
            .post("/v3/api/wallet/get")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("response.message", equalTo("Wallet Details"))
            .body("response.currency_code", equalTo("XOF"));
    }
}
