package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {

    UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object registerHandler(Request request, Response response) throws DataAccessException {
        var serializer = new Gson();
        UserData userData = serializer.fromJson(request.body(), UserData.class);

        if (userData == null || userData.password() == null) {
            throw new DataAccessException("No username and/or password was provided");
        }

        try {
            AuthData authData = userService.register(userData);
            response.status(200);
            return serializer.toJson(authData);
        } catch (DataAccessException e) {
            response.status(403);
            return "User already exists";
        }
    }
}
