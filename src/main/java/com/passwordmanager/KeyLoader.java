package com.passwordmanager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class KeyLoader {

    public static String loadKey(String keyFilePath) {
        try {
            byte[] keyBytes = Files.readAllBytes(Path.of(keyFilePath));
            String keyString = new String(keyBytes, StandardCharsets.UTF_8);
            return keyString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}