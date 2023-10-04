package com.spiro.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PropertiesReader {
    private static PropertiesReader reader;
    private static final String PATH = "src/test/resources/env.properties";
    private static final Logger logger = LogManager.getLogger();

    private int port;
    private String host;
    private Properties props;
    private Environment env;

    public static PropertiesReader getReader() throws IOException {
        if (reader == null) {
            reader = new PropertiesReader();
        }
        return reader;
    }

    private PropertiesReader() throws IOException {
        FileInputStream file = new FileInputStream(PATH);
        this.props = new Properties();
        this.props.load(file);
        file.close();
        setEnv();
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    private void setEnv() {
        String landscape = this.props.getProperty("test.landscape");

        switch (landscape) {
            case "UAT": {
                this.port = Integer.parseInt(this.props.getProperty("uat.port"));
                this.host = this.props.getProperty("uat.host");
                this.env = Environment.UAT;
                break;
            }
            default: {
                this.port = Integer.parseInt(this.props.getProperty("dev.port"));
                this.host = this.props.getProperty("dev.host");
                this.env = Environment.DEV;
            }
            logger.info("** Using {} environment **", landscape);
        }
    }

    public Environment getEnv() {
        return this.env;
    }
}
