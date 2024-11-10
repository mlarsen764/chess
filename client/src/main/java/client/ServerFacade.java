package client;

import com.google.gson.Gson;
import chess.*;
import model.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData registerUser(UserData user) throws Exception {
        String path = "/register";
        return this.makeRequest("POST", path, user, AuthData.class);
    }

    public AuthData loginUser(UserData user) throws Exception {
        String path = "/login";
        return this.makeRequest("POST", path, user, AuthData.class);
    }

    public ChessGame createGame(ChessGame game) throws Exception {
        String path = "/game";
        return this.makeRequest("POST", path, game, ChessGame.class);
    }

    public ChessGame[] listGames() throws Exception {
        String path = "/games";
        record ListGamesResponse(ChessGame[] games) {}
        var response = this.makeRequest("GET", path, null, ListGamesResponse.class);
        return response.games();
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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
