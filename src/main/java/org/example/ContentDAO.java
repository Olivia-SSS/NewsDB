package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/newsdatabase";
    private static final String USER = "olivia";
    private static final String PASSWORD = "2786941n!N";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public List<Map<String, Object>> getMainPageNews(int userId) {
        List<Map<String, Object>> newsList = new ArrayList<>();
        String sql = "SELECT * FROM Content WHERE category IN (SELECT news_preferences FROM Users WHERE user_id = ?) ORDER BY publish_date DESC";
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> newsItem = new HashMap<>();
                    newsItem.put("news_id", resultSet.getInt("news_id"));
                    newsItem.put("news_title", resultSet.getString("news_title"));
                    newsItem.put("cover_image", resultSet.getString("cover_image"));
                    newsItem.put("publish_date", resultSet.getTimestamp("publish_date"));
                    newsItem.put("author", resultSet.getString("author"));
                    newsItem.put("news_body", resultSet.getString("news_body"));
                    newsItem.put("view_count", resultSet.getInt("view_count"));
                    newsItem.put("save_count", resultSet.getInt("save_count"));
                    newsItem.put("share_count", resultSet.getInt("share_count"));
                    newsItem.put("paid_promotion", resultSet.getBoolean("paid_promotion"));
                    newsList.add(newsItem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving main page news list for user: " + e.getMessage());
        }
        return newsList;
    }

    public List<Map<String, Object>> getNewsByCategory(String category) {
        List<Map<String, Object>> newsList = new ArrayList<>();
        String sql = "SELECT * FROM Content WHERE category = ?";

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, category);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> newsItem = new HashMap<>();
                    newsItem.put("news_id", resultSet.getInt("news_id"));
                    newsItem.put("news_title", resultSet.getString("news_title"));
                    newsItem.put("cover_image", resultSet.getString("cover_image"));
                    newsItem.put("publish_date", resultSet.getTimestamp("publish_date"));
                    newsItem.put("author", resultSet.getString("author"));
                    newsItem.put("news_body", resultSet.getString("news_body"));
                    newsItem.put("view_count", resultSet.getInt("view_count"));
                    newsItem.put("save_count", resultSet.getInt("save_count"));
                    newsItem.put("share_count", resultSet.getInt("share_count"));
                    newsItem.put("paid_promotion", resultSet.getBoolean("paid_promotion"));
                    newsItem.put("category", resultSet.getString("category"));
                    newsList.add(newsItem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving news list by category: " + e.getMessage());
        }

        return newsList;
    }

    public List<Map<String, Object>> searchNews(String keyword) {
        List<Map<String, Object>> newsList = new ArrayList<>();
        String sql = "SELECT * FROM Content WHERE news_title LIKE ? OR news_body LIKE ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> newsItem = new HashMap<>();
                    newsItem.put("news_id", rs.getInt("news_id"));
                    newsItem.put("news_title", rs.getString("news_title"));
                    newsItem.put("cover_image", rs.getString("cover_image"));
                    newsItem.put("publish_date", rs.getTimestamp("publish_date"));
                    newsItem.put("author", rs.getString("author"));
                    newsItem.put("news_body", rs.getString("news_body"));
                    newsItem.put("view_count", rs.getInt("view_count"));
                    newsItem.put("save_count", rs.getInt("save_count"));
                    newsItem.put("share_count", rs.getInt("share_count"));
                    newsItem.put("paid_promotion", rs.getBoolean("paid_promotion"));
                    newsList.add(newsItem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching news: " + e.getMessage());
        }

        return newsList;
    }

    public void saveNewsForUser(int userId, int newsId) {
        try (Connection conn = this.connect()) {
            String updateUsersSql = "UPDATE Users SET saved_list = CONCAT(saved_list, ?) WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateUsersSql)) {
                pstmt.setString(1, "," + newsId); // Append news_id to the saved_list
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            }

            String updateContentSql = "UPDATE Content SET save_count = save_count + 1 WHERE news_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateContentSql)) {
                pstmt.setInt(1, newsId);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Error saving news for user: " + e.getMessage());
        }
    }

    public void shareNewsByUser(int userId, int newsId) {
        try (Connection conn = this.connect()) {
            String updateContentSql = "UPDATE Content SET share_count = share_count + 1 WHERE news_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateContentSql)) {
                pstmt.setInt(1, newsId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error recording that a user has shared a news article: " + e.getMessage());
        }
    }

    public boolean publishNews(String title, String body, String author, String category, String image) {
        String sql = "INSERT INTO Content (news_title, news_body, author, category, cover_image, publish_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, body);
            pstmt.setString(3, author);
            pstmt.setString(4, category);
            pstmt.setString(5, image);
            pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // current time for publish_date

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error publishing news: " + e.getMessage());
            return false;
        }
    }
}
