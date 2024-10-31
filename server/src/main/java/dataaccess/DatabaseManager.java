package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Creates the User table if it does not already exist.
     */
    static void createUserTable() throws DataAccessException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS Users (
                username VARCHAR(50) PRIMARY KEY,
                passwordHash VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE,
                createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating Users table: " + e.getMessage());
        }
    }

    /**
     * Creates the Auth table if it does not already exist.
     */
    static void createAuthTable() throws DataAccessException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS Auth (
                authID INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL,
                token VARCHAR(255) NOT NULL UNIQUE,
                expiration TIMESTAMP NOT NULL,
                FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
            );
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating Auth table: " + e.getMessage());
        }
    }

    static void createGameTable() throws DataAccessException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS Game (
                gameID INT PRIMARY KEY,
                whiteUsername VARCHAR(50) NOT NULL,
                blackUsername VARCHAR(50),
                gameName VARCHAR(100),
                gameState TEXT,
                FOREIGN KEY (whiteUsername) REFERENCES Users(username),
                FOREIGN KEY (blackUsername) REFERENCES Users(username)
            );
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating Game table: " + e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}