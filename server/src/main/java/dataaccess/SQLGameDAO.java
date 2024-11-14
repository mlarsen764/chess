package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.*;
import java.util.*;

public class SQLGameDAO implements GameDAO{
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM Game";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games:" + e.getMessage());
        }
    }

    @Override
    public void createGame(GameData g) throws DataAccessException {
        String sql = "INSERT INTO Game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        String serializedGame = serializeGame(g.game());
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, g.gameID());
            stmt.setString(2, g.whiteUsername());
            stmt.setString(3, g.blackUsername());
            stmt.setString(4, g.gameName());
            stmt.setString(5, serializedGame);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game:" + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM Game WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (var results = stmt.executeQuery()) {
                if (results.next()) {
                    var whiteUsername = results.getString("whiteUsername");
                    var blackUsername = results.getString("blackUsername");
                    var gameName = results.getString("gameName");
                    var game = deserializeGame(results.getString("game"));
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                } else {
                    throw new DataAccessException("Game not found");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game:" + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM Game";
        Collection<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             try (var results = stmt.executeQuery()) {
                while(results.next()) {
                    var gameID = results.getInt("gameID");
                    var whiteUsername = results.getString("whiteUsername");
                    var blackUsername = results.getString("blackUsername");
                    var gameName = results.getString("gameName");
                    var game = deserializeGame(results.getString("game"));
                    games.add (new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games" + e.getMessage());
        }
        return games;
    }

    @Override
    public void updateGame(GameData g) throws DataAccessException {
        String sql = "UPDATE Game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        String serializedGame = serializeGame(g.game());
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, g.whiteUsername());
            stmt.setString(2, g.blackUsername());
            stmt.setString(3, g.gameName());
            stmt.setString(4, serializedGame);
            stmt.setInt(5, g.gameID());
            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                throw new DataAccessException("Game not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game:" + e.getMessage());
        }
    }

    private String serializeGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame deserializeGame(String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }
}
