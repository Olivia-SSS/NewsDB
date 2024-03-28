package org.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/newsdatabase";
    private static final String USER = "olivia";
    private static final String PASSWORD = "2786941n!N";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public void createUserByEmail(String email, String password, String phone, String username, String profile_photo) throws SQLException {
        String sql = "INSERT INTO Users (email, phone, profile_photo, username, password, saved_list, news_preferences, browsing_history) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, phone);
            statement.setString(3, profile_photo);
            statement.setString(4, username);
            statement.setString(5, password);
            statement.setString(6, "");
            statement.setString(7, "");
            statement.setString(8, "");

            statement.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    public void updateUserInfo(String userId, String newNickname, String newPhone) {
        String sql = "UPDATE Users SET username = ?, phone = ? WHERE user_id = ?";

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, newNickname);
            statement.setString(2, newPhone);
            statement.setString(3, userId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User account information updated successfully.");
            } else {
                System.out.println("No user found with the specified user ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating user account information: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getUserBrowsingHistory(int userId) {
        List<Map<String, Object>> browsingHistory = new ArrayList<>();
        String sql = "SELECT browsing_history FROM Users WHERE user_id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String browsingHistoryStr = rs.getString("browsing_history");
                    browsingHistory = parseBrowsingHistory(browsingHistoryStr);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user browsing history: " + e.getMessage());
        }
        return browsingHistory;
    }

    private List<Map<String, Object>> parseBrowsingHistory(String browsingHistoryStr) {
        List<Map<String, Object>> browsingHistory = new ArrayList<>();

        if (browsingHistoryStr != null && !browsingHistoryStr.isEmpty()) {
            String[] historyArray = browsingHistoryStr.split(",");

            for (String newsIdStr : historyArray) {
                try {
                    int newsId = Integer.parseInt(newsIdStr.trim());
                    Map<String, Object> browseItem = new HashMap<>();
                    browseItem.put("news_id", newsId);

                    browsingHistory.add(browseItem);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing browsing history item: " + e.getMessage());
                }
            }
        }

        return browsingHistory;
    }

}
