package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/newsdatabase";
    private static final String USER = "olivia";
    private static final String PASSWORD = "2786941n!N";

    public static void main(String[] args) {
        // Register JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        // Open a connection
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // create user table
            String createUserTable = "CREATE TABLE IF NOT EXISTS Users (" +
                                     "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                     "email VARCHAR(255) UNIQUE NOT NULL, " +
                                     "phone VARCHAR(15) UNIQUE NOT NULL, " +
                                     "profile_photo VARCHAR(255), " +
                                     "username VARCHAR(50), " +
                                     "password VARCHAR(255) NOT NULL, " +
                                     "saved_list TEXT, " +
                                     "news_preferences TEXT, " +
                                     "browsing_history TEXT, " +
                                     "last_login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                     ")";

            stmt.executeUpdate(createUserTable);

            // create content table
            String createContentTable = "CREATE TABLE IF NOT EXISTS Content (" +
                                        "news_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                        "news_title VARCHAR(255) NOT NULL, " +
                                        "cover_image VARCHAR(255), " +
                                        "publish_date DATETIME NOT NULL, " +
                                        "author VARCHAR(100), " +
                                        "news_body TEXT NOT NULL, " +
                                        "view_count INT DEFAULT 0, " +
                                        "save_count INT DEFAULT 0, " +
                                        "share_count INT DEFAULT 0, " +
                                        "paid_promotion BOOLEAN DEFAULT FALSE, " +
                                        "category VARCHAR(100) NOT NULL" +
                                        ")";
            stmt.executeUpdate(createContentTable);

            // create comment table
            String createCommentTable = "CREATE TABLE IF NOT EXISTS Comments (" +
                                        "comment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                        "user_id INT, " +
                                        "news_id INT, " +
                                        "content TEXT NOT NULL, " +
                                        "comment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "reply_to_comment_id INT, " +
                                        "like_count INT DEFAULT 0, " +
                                        "unlike_count INT DEFAULT 0, " +
                                        "FOREIGN KEY (user_id) REFERENCES Users(user_id), " +
                                        "FOREIGN KEY (news_id) REFERENCES Content(news_id)" +
                                        ")";
            stmt.executeUpdate(createCommentTable);

            // create advertisement table
            String createAdvertisementsTable = "CREATE TABLE IF NOT EXISTS Advertisements (" +
                                                "ad_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                                "ad_title VARCHAR(255), " +
                                                "ad_content TEXT, " +
                                                "ad_image_link VARCHAR(255), " +
                                                "placement ENUM('splash', 'popup', 'in_feed', 'in_comment', 'in_article', 'end_of_article', 'recommendation') NOT NULL, " +
                                                "advertiser_id VARCHAR(255), " +
                                                "active_until DATETIME, " +
                                                "region_restriction VARCHAR(255), " +
                                                "device_restriction VARCHAR(255), " +
                                                "click_count INT DEFAULT 0, " +
                                                "exposure_count INT DEFAULT 0, " +
                                                "ad_weight INT, " +
                                                "ad_category VARCHAR(255) " +
                                                ")";
            stmt.executeUpdate(createAdvertisementsTable);

            System.out.println("Tables Created");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}