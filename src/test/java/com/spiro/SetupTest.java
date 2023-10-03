package com.spiro;

import java.io.IOException;

import org.testng.annotations.BeforeSuite;

import com.spiro.utils.CsvUtils;
import com.spiro.utils.PropertiesReader;

import io.restassured.RestAssured;


public class SetupTest {

    /**
     * Load properties and data required for running tests.
     * This is executed before all the tests in EnergyPlan test suite. (defined in testng.xml)
     */
    @BeforeSuite
    public void setup() {
        System.out.println("Running Test setup...");

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

        System.out.println("Setup Done!");
    }
}
