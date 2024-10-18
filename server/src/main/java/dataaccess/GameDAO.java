package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
//    createGame: Create a new game.
//    getGame: Retrieve a specified game with the given game ID.
//    listGames: Retrieve all games.
//    updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID.
//      This is used when players join a game or when a move is made.
    void clear() throws DataAccessException;
    void createGame(GameData g) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(GameData g) throws DataAccessException;
}
