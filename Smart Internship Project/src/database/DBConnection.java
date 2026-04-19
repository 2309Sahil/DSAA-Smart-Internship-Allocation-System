package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/smart_internship_db";
    private static final String USER = "root";
    private static final String PASSWORD = "India$2309";

    public static Connection getConnection() {

        Connection conn = null;

        try {

            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Database connected successfully!");

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();

        }

        return conn;
    }
}