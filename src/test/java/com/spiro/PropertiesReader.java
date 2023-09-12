package com.spiro;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {
    private static PropertiesReader reader;
    private static final String PATH = "src/test/resources/env.properties";

    private int port;
    private String host;
    private Properties props;

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
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void useDevEnv() {
    	this.port = Integer.parseInt(this.props.getProperty("dev.port"));
        this.host = this.props.getProperty("dev.host");
    }

    public void useTestEnv() {
        this.port = Integer.parseInt(this.props.getProperty("uat.port"));
        this.host = this.props.getProperty("uat.host");
    }
}
