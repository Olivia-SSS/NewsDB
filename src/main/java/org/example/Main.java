package org.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();
        ContentDAO contentDAO = new ContentDAO();
        CommentDAO commentDAO = new CommentDAO();
        AdDAO adDAO = new AdDAO();

        // Test for UserDAO
        // Test creating a new user
        try {
            userDAO.createUserByEmail("test4@example.com", "password123", "1672628293", "test4user", "profile2.jpg");
            System.out.println("User created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }

        // Test updating a user information
        userDAO.updateUserInfo("3", "newnickname", "0987654321");

        // Test retrieving user browsing history
        List<Map<String, Object>> browsingHistory = userDAO.getUserBrowsingHistory(1);
        if (!browsingHistory.isEmpty()) {
            System.out.println("Browsing History: " + browsingHistory);
        } else {
            System.out.println("No browsing history found.");
        }

        // Test ContentDAO
        // Test publishing news
        boolean isPublished = contentDAO.publishNews("New Discoveries in Space", "Astronomers found...", "Jane Doe", "Science", "space_cover.jpg");
        if (isPublished) {
            System.out.println("News published successfully.");
        } else {
            System.out.println("Failed to publish news.");
        }

        // Test saving news for a user
        contentDAO.saveNewsForUser(1, 1); // Assuming '1' is user_id and '101' is news_id

        // Test marking a news article as shared by a user
        contentDAO.shareNewsByUser(1, 1); // Assuming '1' is user_id and '101' is news_id

        // Test getting main page news for a specific user
        List<Map<String, Object>> mainPageNews = contentDAO.getMainPageNews(4); // Assuming '1' is a valid user_id
        System.out.println("Main Page News: " + mainPageNews);

        // Test getting news by category
        List<Map<String, Object>> newsByCategory = contentDAO.getNewsByCategory("Science"); // Example category
        System.out.println("News by Category: " + newsByCategory);

        // Test searching news
        List<Map<String, Object>> searchedNews = contentDAO.searchNews("Space"); // Example search keyword
        System.out.println("Searched News: " + searchedNews);

        // Test ContentDAO
        // Add a comment to news
        commentDAO.addCommentToNews(1, 1, "This is a comment text!");
        System.out.println("Comment added successfully.");

        // Retrieve comments for a specific news item
        List<Map<String, Object>> commentsList = commentDAO.getCommentsByNewsId(1);
        System.out.println("Comments for news ID 1:");
        for (Map<String, Object> comment : commentsList) {
            System.out.println(comment);
        }

        // Like a comment
        commentDAO.likeOrUnlikeComment(1, 5, true);
        System.out.println("Comment liked successfully.");

        // Unlike a comment
        commentDAO.likeOrUnlikeComment(1, 5, false);
        System.out.println("Comment unliked successfully.");

        // Test retrieving user comment history
        List<Map<String, Object>> commentHistory = commentDAO.getCommentHistory(1);
        if (!commentHistory.isEmpty()) {
            System.out.println("Comment History: " + commentHistory);
        } else {
            System.out.println("No comment history found.");
        }

        // Delete a comment
        commentDAO.deleteComment(1, 5);
        System.out.println("Comment deleted successfully.");

        //Test AdDAO
        // Test getAdvertisementsByCategory
        System.out.println("Testing getAdvertisementsByCategory:");
        List<Map<String, Object>> adsByCategory = adDAO.getAdvertisementsByCategory("Science");
        for (Map<String, Object> ad : adsByCategory) {
            System.out.println(ad);
        }

        // Test publishAdvertisement
        System.out.println("Testing publishAdvertisement:");
        boolean adPublished = adDAO.publishAdvertisement(
                "New Smartwatch Release",
                "Check out our latest smartwatch with 24h battery life!",
                "http://example.com/smartwatch.jpg",
                "in_feed",
                "advertiser123",
                "Electronics",
                new Timestamp(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000), // Active for one week
                "Global",
                "All",
                1
        );

        if (adPublished) {
            System.out.println("Advertisement published successfully.");
        } else {
            System.out.println("Failed to publish advertisement.");
        }

    }
}

