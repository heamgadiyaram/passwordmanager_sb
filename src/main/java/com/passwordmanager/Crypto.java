package com.passwordmanager;
import java.nio.file.*;
import java.util.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;


public class Crypto {

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String KEY_FILE_PATH = "secure/encryption-key.txt";

    static {
        generateEncryptionKey();
    }
    
    public static void generateEncryptionKey() {
        try {
            Path keyPath = Path.of(KEY_FILE_PATH);
            if (!Files.exists(keyPath)) {
                Files.createDirectories(keyPath.getParent());
                SecretKey secretKey = generateKey();

                saveKeyToFile(secretKey, KEY_FILE_PATH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(256, secureRandom); 
        return keyGenerator.generateKey();
    }

    private static void saveKeyToFile(SecretKey secretKey, String filePath) {
        byte[] encodedKey = secretKey.getEncoded();
        String encodedKeyString = Base64.getEncoder().encodeToString(encodedKey);

        try {
            Path path = Path.of(filePath);
            Files.createDirectories(path.getParent()); 
            Files.write(path, encodedKeyString.getBytes(), StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKey loadEncryptionKey() {
        try {
            String keyString = KeyLoader.loadKey(KEY_FILE_PATH);
            if(keyString != null){
                byte[] keyBytes = Base64.getDecoder().decode(keyString);
                return new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
            } 
            else {
                return null;
            }
            
        } 
        catch (Exception e) {
            return null;
        }
    }

    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, loadEncryptionKey());
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, loadEncryptionKey());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
