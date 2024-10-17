package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private Map<Integer, GameData> gameMap = new HashMap<>();
    @Override
    public void clear() {
        gameMap.clear();
    }

    @Override
    public void createGame(GameData g) throws DataAccessException {
        if (gameMap.containsKey(g.gameID())) {
            throw new DataAccessException("Game already exists");
        }
        gameMap.put(g.gameID(), g);
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        GameData game = gameMap.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    @Override
    public Collection<GameData> listGames() {
        return gameMap.values();
    }

    @Override
    public void updateGame(GameData g) throws DataAccessException {
        if (!gameMap.containsKey(g.gameID())) {
            throw new DataAccessException("Game not found");
        }
        gameMap.put(g.gameID(), g);
    }
}
