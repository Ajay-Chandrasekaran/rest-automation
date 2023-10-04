package com.spiro;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;

import io.restassured.RestAssured;

import com.spiro.utils.CsvUtils;
import com.spiro.utils.PropertiesReader;


public class SetupTest {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Load properties and data required for running tests.
     * This is executed before all the tests in EnergyPlan test suite. (defined in testng.xml)
     */
    @BeforeSuite
    public void setup() {
        logger.info("Setting up tests.");

        PropertiesReader prop = null;

        try {
            prop = PropertiesReader.getReader();
        } catch (IOException e) {
            logger.fatal(e.getMessage(), e);
            System.exit(-1);
        }

        RestAssured.baseURI = prop.getHost();
        RestAssured.port = prop.getPort();

        try {
            CsvUtils.loadCustomers(prop.getEnv());
        } catch (Exception e) {
            logger.fatal(e.getMessage(), e);
            System.exit(-1);
        }

        logger.info("Setup completed.");
    }
}
