package com.passwordmanager;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.security.*;

import java.sql.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String username;
    private String passwordHash;
    private String salt;
    private int userId;

    public User(String username, String password, boolean isLogin) {
        this.username = username;

        if (isLogin) {
            if (!loadUserData(password)) {
                System.out.println("Invalid username or password.");
                System.exit(0);
            }
        } else {
            if (isUsernameTaken()) {
                System.out.println("Username already taken. Please choose another one.");
                System.exit(0);
            } else {
                this.salt = generateSalt();
                this.passwordHash = hash(password);
                saveUserToDatabase();
                System.out.println("New user registered successfully.");
            }
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public String getSalt() {
        return this.salt;
    }

    public String hash(String input) {
        String saltedInput = salt + input;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedInput.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveUserToDatabase() {
        String query = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
        try {

            Connection conn = Database.getConnection();
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, salt);
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                this.userId = rs.getInt(1);
            }
            System.out.println("User registered successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean loadUserData(String password) {
        String query = "SELECT id, password_hash, salt FROM users WHERE username = ?";
        try {
            Connection conn = Database.getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                this.userId = rs.getInt("id");
                this.salt = rs.getString("salt");
                this.passwordHash = rs.getString("password_hash");

                if (!this.passwordHash.equals(hash(password))) {
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addService(String service, String password) {
        if (getService(service) != null) {
            System.out.println("Service already exists.");
            return;
        }

        String encryptedPassword = Crypto.encrypt(password);
        String query = "INSERT INTO services (user_id, service_name, encrypted_password) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setString(2, service);
            statement.setString(3, encryptedPassword);
            statement.executeUpdate();
            System.out.println("Password for " + service + " added.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getService(String service) {
        String query = "SELECT encrypted_password FROM services WHERE user_id = ? AND service_name = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, service);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return Crypto.decrypt(rs.getString("encrypted_password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteService(String service) {
        String query = "DELETE FROM services WHERE user_id = ? AND service_name = ?";

        try {

            Connection conn = Database.getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, service);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Service \"" + service + "\" deleted.");
            } else {
                System.out.println("Service \"" + service + "\" not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isUsernameTaken() {
        String query = "SELECT id FROM users WHERE username = ?";
        try {

            Connection conn = Database.getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
