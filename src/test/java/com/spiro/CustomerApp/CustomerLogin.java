package com.spiro.CustomerApp;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import com.spiro.utils.PropertiesReader;
import io.restassured.RestAssured;

public class CustomerLogin {

    @BeforeAll
    public static void setup() throws IOException {
        PropertiesReader propReader = PropertiesReader.getReader();
        propReader.setEnv("uat");
        RestAssured.baseURI = propReader.getHost();
        RestAssured.port = propReader.getPort();
    }

    @AfterAll
    public static void teardown() {
        RestAssured.reset();
    }
    
    public void loginCustomerAppTest() {
        
       RestAssured.
                   given()
                   .multiPart("dial_code", +228)
                   .multiPart("mobile_number",93535477)
                   .when().
                   post("/v3/api/driver/login?=");
                 
    }
    
    
    
    
}
