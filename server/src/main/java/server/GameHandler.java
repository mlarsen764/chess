package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GameHandler {

    private GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGamesHandler(Request request, Response response) {
        var serializer = new Gson();
        String authToken = request.headers("Authorization");

        if (authToken == null) {
            response.status(401);
            return serializer.toJson(Map.of("message", "Error: unauthorized"));
        }

        try {
            var games = gameService.listGames(authToken);
            response.status(200);
            return serializer.toJson(Map.of("games", games));
        } catch (DataAccessException e) {
            response.status(401);
            return serializer.toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            response.status(500);
            return serializer.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object createGameHandler(Request request, Response response) {
        var serializer = new Gson();
        String authToken = request.headers("Authorization");
        GameData gameData = serializer.fromJson(request.body(), GameData.class);
        int gameID = ThreadLocalRandom.current().nextInt(1, 1000);
        GameData createGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());

        if (authToken == null || gameData == null || gameData.gameName() == null) {
            response.status(400);
            return serializer.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            GameData createdGame = gameService.createGame(authToken, createGameData);
            response.status(200);
            return serializer.toJson(Map.of("gameID", createdGame.gameID()));
        } catch (DataAccessException e) {
            response.status(401);
            return serializer.toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            response.status(500);
            return serializer.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object joinGameHandler(Request request, Response response) throws DataAccessException {
        var serializer = new Gson();
        String authToken = request.headers("Authorization");
        Map<String, Object> requestBody = serializer.fromJson(request.body(), Map.class);

        String playerColor = (String) requestBody.get("playerColor");

        if (authToken == null || playerColor == null || !request.body().contains("\"gameID\":")) {
            response.status(400);
            return serializer.toJson(Map.of("message", "Error: bad request"));
        }

        int gameID = ((Double) requestBody.get("gameID")).intValue();
        ChessGame game = gameService.getChessGame(gameID);

        try {
            gameService.joinGame(authToken, gameID, playerColor, game);
            response.status(200);
            return "{}";
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Unauthorized")) {
                response.status(401);
            } else if (e.getMessage().contains("Already taken")) {
                response.status(403);
            } else {
                response.status(400);
            }
            return serializer.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            response.status(500);
            return serializer.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
