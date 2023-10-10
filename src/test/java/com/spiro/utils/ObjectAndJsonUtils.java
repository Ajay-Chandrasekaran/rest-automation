package com.spiro.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

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

    public static <T> T createObjectFromJsonFile(String path, Class<T> classType) throws IOException {
        File f = new File(path);
        ObjectMapper oMapper = new ObjectMapper();
        T obj = oMapper.readValue(f, classType);
        return obj;
    }

    public static String UUIDgenerator() {
        UUID randomUUID = UUID.randomUUID();
        String string = randomUUID.toString();
        return string;
    }
}
