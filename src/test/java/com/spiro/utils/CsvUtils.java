package com.spiro.utils;

import java.util.Iterator;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.opencsv.CSVReader;


public class CsvUtils {
    private static Iterator<String[]> customers = null;
    private static final String UAT_CSV = "src/test/resources/csv/uat_customers.csv";
    private static final String DEV_CSV = "src/test/resources/csv/dev_customers.csv";

    /**
     * @param env Testing environment.
     * @throws Exception In case of error while handling files.
     */
    public static void loadCustomers(Environment env) throws Exception {
        String path = (Environment.UAT == env)? UAT_CSV : DEV_CSV;

        try (Reader reader = Files.newBufferedReader(Path.of(path))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                CsvUtils.customers = csvReader.readAll().iterator();
            }
        }
    }

    /**
     * Gets the next customer id on the list
     *
     * @return Customer id if it exists, null otherwise.
     */
    public static String getNextCustomer() {
        // TODO: how long is this stored in memory ?
        // TODO: Should customer Id be read from file lazily as required ?
        return (CsvUtils.customers.hasNext())? CsvUtils.customers.next()[0] : null;
    }
}
