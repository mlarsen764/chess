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
        String statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage());
        }
    }

    static void dropDatabase() throws DataAccessException {
        String sql = "DROP DATABASE IF EXISTS " + DATABASE_NAME;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error dropping database: " + e.getMessage());
        }
    }

    /**
     * Creates the User table if it does not already exist.
     */
    static void createUserTable() throws DataAccessException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS Users (
                username VARCHAR(50) PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE
            );
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating Users table: " + e.getMessage());
        }
    }

    public static void dropUserTable() throws DataAccessException {
        String dropTableSQL = "DROP TABLE IF EXISTS Users;";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(dropTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error dropping Users table: " + e.getMessage());
        }
    }

    public static void printUserTableColumns() throws DataAccessException {
        String sql = "SELECT * FROM Users LIMIT 1"; // Using LIMIT 1 to avoid fetching too many rows
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            System.out.println("Columns in Users table:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("Column %d: %s (Type: %s)\n", i, metaData.getColumnName(i), metaData.getColumnTypeName(i));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching columns of Users table: " + e.getMessage());
        }
    }

    /**
     * Creates the Auth table if it does not already exist.
     */
    static void createAuthTable() throws DataAccessException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS Auth (
                username VARCHAR(50) NOT NULL,
                authToken VARCHAR(255) NOT NULL UNIQUE,
                PRIMARY KEY (authToken)
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
                whiteUsername VARCHAR(50),
                blackUsername VARCHAR(50),
                gameName VARCHAR(100),
                game TEXT
            );
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating Game table: " + e.getMessage());
        }
    }

    public static void initializeDatabase() throws DataAccessException {
        createDatabase();
        createUserTable();
        createAuthTable();
        createGameTable();
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
