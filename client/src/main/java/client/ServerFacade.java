package client;

import com.google.gson.Gson;
import chess.*;
import exception.ResponseException;
import model.*;
import requests.*;
import results.*;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegistrationResult register(RegistrationRequest request) throws ResponseException {
        String path = "/user";
        System.out.println(this.makeRequest("POST", path, request, RegistrationResult.class, null));
        return this.makeRequest("POST", path, request, RegistrationResult.class, null);
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        String path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class, null);
    }

    public LogoutResult logout(LoginResult loginResult) throws ResponseException {
        String path = "/session";
        return this.makeRequest("DELETE", path, loginResult, LogoutResult.class, null);
    }

    public CreateGameResult createGame(CreateGameRequest request, LoginResult loginResult) throws ResponseException {
        String path = "/game";
        return this.makeRequest("POST", path, request, CreateGameResult.class, loginResult);
    }

    public JoinGameResult joinGame(JoinGameRequest request, LoginResult loginResult) throws ResponseException {
        String path = "/game";
        return this.makeRequest("PUT", path, request, JoinGameResult.class, loginResult);
    }

    public ListGamesResult listGames(ListGamesRequest request, LoginResult loginResult) throws ResponseException {
        String path = "/game";
        return this.makeRequest("GET", path, request, ListGamesResult.class, loginResult);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, LoginResult loginResult) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (loginResult != null) {
                http.addRequestProperty("authorization", loginResult.authToken());
            }

            if (!Objects.equals(method, "GET")) {
                http.setDoOutput(true);
                writeBody(request, http);
            }

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
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
