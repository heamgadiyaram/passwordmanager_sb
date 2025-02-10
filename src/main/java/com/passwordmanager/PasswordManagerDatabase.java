package com.passwordmanager;
import java.sql.*;
import java.util.*;


public class PasswordManagerDatabase {
    private Integer getUserId(String username, String passwordHash) {
        String query = "SELECT id FROM users WHERE username = ? AND password_hash = ?";
        
        try {
            Connection conn = Database.getConnection();
            PreparedStatement statement = conn.prepareStatement(query); 
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } 
            catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> retrieveServices(String username, String passwordHash) {
        Map<String, String> services = new HashMap<>();
        Integer userId = getUserId(username, passwordHash);
        if (userId == null) return services;

        String query = "SELECT service_name, encrypted_password FROM services WHERE user_id = ?";
        try {
            Connection conn = Database.getConnection();
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                services.put(rs.getString("service_name"), rs.getString("encrypted_password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
}
