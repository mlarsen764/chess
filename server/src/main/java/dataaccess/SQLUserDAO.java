package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users:" + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
        String sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, u.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating user:" + u.username());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserData(rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getString("email"));
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user:" + e.getMessage());
        }
    }

    public boolean verifyUser(String username, String providedPassword) throws DataAccessException {
        String sql = "SELECT password FROM Users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                return BCrypt.checkpw(providedPassword, storedHashedPassword);
            }
            return false;
        } catch (SQLException e) {
            throw new DataAccessException("Error verifying user: " + e.getMessage());
        }
    }
}
