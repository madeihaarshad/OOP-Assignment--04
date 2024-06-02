package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university?useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "Diyasatti@08";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
