package com.spiro.utils;

import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectAndJsonUtils {

    public static JsonNode getJsonNodeFromFile(String path) throws IOException {
        JsonNode node = null;

        try (FileReader fr = new FileReader(path)) {
            ObjectMapper oMapper = new ObjectMapper();
            node = oMapper.readTree(fr);
        } catch (IOException e) {
            throw e;
        }

        return node;
    }

    public static JsonNode getJsonNodeFromString(String json) throws IOException {
        return new ObjectMapper().readTree(json);
    }
}
