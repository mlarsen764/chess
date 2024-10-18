package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public class GameService {
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ChessGame getChessGame(int gameID) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        return game.game();
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }
        return gameDAO.listGames();
    }

    public GameData createGame(String authToken, GameData gameData) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }
        gameDAO.createGame(gameData);
        return gameData;
    }

    public void joinGame(String authToken, int gameID, String playerColor, ChessGame chessGame) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }

        AuthData authData = authDAO.getAuth(authToken);
        GameData game = gameDAO.getGame(gameID);

        if (playerColor.equalsIgnoreCase("WHITE") && game.whiteUsername() == null) {
            game = new GameData(game.gameID(), authData.username(), game.blackUsername(), game.gameName(), chessGame);
        } else if (playerColor.equalsIgnoreCase("BLACK") && game.blackUsername() == null) {
            game = new GameData(game.gameID(), game.whiteUsername(), authData.username(), game.gameName(), chessGame);
        } else {
            throw new DataAccessException("Already taken");
        }

        gameDAO.updateGame(game);
    }
}
