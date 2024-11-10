package client;

import com.google.gson.Gson;
import chess.*;
import model.*;
import requests.JoinGameRequest;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.List;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData user) throws Exception {
        String path = "/register";
        return this.makeRequest(path, user, AuthData.class);
    }

    public AuthData login(UserData user) throws Exception {
        String path = "/login";
        return this.makeRequest(path, user, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        String path = "/logout";
        this.makeRequest(path, new AuthData(authToken, null), null);
    }

    public GameData createGame(String authToken, GameData gameData) throws Exception {
        String path = "/game";
        return this.makeRequestWithAuth("POST", path, authToken, gameData, GameData.class);
    }

    public void joinGame(String authToken, int gameID, String playerColor, ChessGame chessGame) throws Exception {
        String path = String.format("/game/%d/join", gameID);
        var request = new JoinGameRequest(authToken, playerColor, gameID);
        this.makeRequestWithAuth("POST", path, authToken, request, null);
    }

    public Collection<GameData> listGames(String authToken) throws Exception {
        String path = "/games";
        record listGamesResponse(GameData[] game) {
        }
        var response = this.makeRequestWithAuth("GET", path, authToken, null, listGameResponse.class);
        return List.of(response.game());
    }


    private <T> T makeRequest(String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception();
        }
    }

    private <T> T makeRequestWithAuth(String method, String path, String authToken, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", "Bearer " + authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception();
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    // Helper method to check for successful response
    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new Exception();
        }
    }

    // Helper method to read JSON response body
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() > 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
