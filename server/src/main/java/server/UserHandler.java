package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.Objects;

public class UserHandler {

    UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object registerHandler(Request request, Response response) throws DataAccessException {
        var serializer = new Gson();
        UserData userData = serializer.fromJson(request.body(), UserData.class);

        if (userData == null || userData.username() == null || userData.password() == null) {
            response.status(400);
            return serializer.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            AuthData authData = userService.register(userData);
            response.status(200);
            return serializer.toJson(authData);
        } catch (DataAccessException e) {
            response.status(403);
            return serializer.toJson(Map.of("message", "Error: already taken"));
        } catch (Exception e) {
            response.status(500);
            return serializer.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object loginHandler(Request request, Response response) throws DataAccessException {
        var serializer = new Gson();
        UserData userData = serializer.fromJson(request.body(), UserData.class);

        if (userData == null || userData.username() == null || userData.password() == null) {
            response.status(400);
            return serializer.toJson(Map.of("message", "Error: bad request"));
        }

        try {
            AuthData authData = userService.login(userData);
            response.status(200);
            return serializer.toJson(authData);
        } catch (DataAccessException e) {
            response.status(401);
            return serializer.toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            response.status(500);
            return serializer.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object logoutHandler(Request request, Response response) throws DataAccessException {
        var serializer = new Gson();
        String authToken = request.headers("Authorization");

        if (authToken == null) {
            response.status(400);
            return serializer.toJson(Map.of("message", "Error: missing authorization token"));
        }

        try {
            userService.logout(authToken);
            response.status(200);
            return serializer.toJson(Map.of("message", "Successfully logged out"));
        } catch (DataAccessException e) {
            response.status(401);
            return serializer.toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            response.status(500);
            return serializer.toJson(Map.of("message", "Error:" + e.getMessage()));
        }
    }
}
