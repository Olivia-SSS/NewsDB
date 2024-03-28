package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CommentDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/newsdatabase";
    private static final String USER = "olivia";
    private static final String PASSWORD = "2786941n!N";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public void addCommentToNews(int userId, int newsId, String commentText) {
        String sql = "INSERT INTO Comments (user_id, news_id, content) VALUES (?, ?, ?);";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, newsId);
            pstmt.setString(3, commentText);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding comment to news: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getCommentsByNewsId(int newsId) {
        List<Map<String, Object>> commentsList = new ArrayList<>();
        String commentsSql = "SELECT * FROM Comments WHERE news_id = ? ORDER BY like_count DESC";

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(commentsSql)) {
            statement.setInt(1, newsId);
            try (ResultSet commentsResultSet = statement.executeQuery()) {
                while (commentsResultSet.next()) {
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("comment_id", commentsResultSet.getInt("comment_id"));
                    comment.put("user_id", commentsResultSet.getInt("user_id"));
                    comment.put("news_id", commentsResultSet.getInt("news_id"));
                    comment.put("content", commentsResultSet.getString("content"));
                    comment.put("comment_time", commentsResultSet.getTimestamp("comment_time"));
                    comment.put("reply_to_comment_id", commentsResultSet.getInt("reply_to_comment_id"));
                    comment.put("like_count", commentsResultSet.getInt("like_count"));
                    comment.put("unlike_count", commentsResultSet.getInt("unlike_count"));
                    commentsList.add(comment);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving comments for news: " + e.getMessage());
        }
        return commentsList;
    }

    public void likeOrUnlikeComment(int userId, int commentId, boolean like_or_not) {
        // This SQL increments the like or unlike count based on the user's action
        String sql = like_or_not ?
                "UPDATE Comments SET `like_count` = `like_count` + 1 WHERE comment_id = ? AND user_id = ?" :
                "UPDATE Comments SET `unlike_count` = `unlike_count` + 1 WHERE comment_id = ? AND user_id = ?";

        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, commentId);
            statement.setInt(2, userId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Like or Dislike the comment failed, no rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("Error rating the comment: " + e.getMessage());
        }
    }

    public void deleteComment(int userId, int commentId) {
        String sql = "DELETE FROM Comments WHERE comment_id = ? AND user_id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commentId);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting the comment failed, no rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting the comment: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getCommentHistory(int userId) {
        List<Map<String, Object>> commentHistory = new ArrayList<>();
        String sql = "SELECT * FROM Comments WHERE user_id = ? ORDER BY comment_time DESC";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("comment_id", rs.getInt("comment_id"));
                    comment.put("user_id", rs.getInt("user_id"));
                    comment.put("news_id", rs.getInt("news_id"));
                    comment.put("content", rs.getString("content"));
                    comment.put("comment_time", rs.getTimestamp("comment_time"));
                    comment.put("like_count", rs.getInt("like_count"));
                    comment.put("unlike_count", rs.getInt("unlike_count"));
                    commentHistory.add(comment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user comment history: " + e.getMessage());
        }
        return commentHistory;
    }



}
