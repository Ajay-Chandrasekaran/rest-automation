package com.wissen.CustomerApp;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.wissen.PropertiesReader;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class SimpleCustomerTest {

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
}
