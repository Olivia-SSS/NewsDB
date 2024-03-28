package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/newsdatabase";
    private static final String USER = "olivia";
    private static final String PASSWORD = "2786941n!N";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public List<Map<String, Object>> getAdvertisementsByCategory(String category) {
        List<Map<String, Object>> adsList = new ArrayList<>();
        String sql = "SELECT * FROM Advertisements WHERE ad_category = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> ad = new HashMap<>();
                    ad.put("ad_id", rs.getInt("ad_id"));
                    ad.put("ad_title", rs.getString("ad_title"));
                    ad.put("ad_content", rs.getString("ad_content"));
                    ad.put("ad_image_link", rs.getString("ad_image_link"));
                    adsList.add(ad);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving advertisements by category: " + e.getMessage());
        }

        return adsList;
    }

    public boolean publishAdvertisement(String title, String content, String imageLink, String placement, String advertiserId, String category, Timestamp activeUntil, String regionRestriction, String deviceRestriction, int adWeight) {
        String sql = "INSERT INTO Advertisements (ad_title, ad_content, ad_image_link, placement, advertiser_id, ad_category, active_until, region_restriction, device_restriction, ad_weight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, imageLink);
            pstmt.setString(4, placement);
            pstmt.setString(5, advertiserId);
            pstmt.setString(6, category);
            pstmt.setTimestamp(7, activeUntil);
            pstmt.setString(8, regionRestriction);
            pstmt.setString(9, deviceRestriction);
            pstmt.setInt(10, adWeight);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error publishing advertisement: " + e.getMessage());
            return false;
        }
    }

}
