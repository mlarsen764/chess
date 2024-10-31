package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Auth";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth data:" + e.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO Auth (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth:" + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM Auth WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            } else {
                throw new DataAccessException("Unauthorized");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth data:" + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM Auth WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            int deletedRows = stmt.executeUpdate();
            if (deletedRows == 0) {
                throw new DataAccessException("Unauthorized");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth data:" + e.getMessage());
        }
    }
}
