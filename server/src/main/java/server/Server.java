package server;

import dataaccess.*;
import service.UserService;
import spark.*;

public class Server {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    UserService userService;

    UserHandler userHandler;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userService = new UserService(userDAO, authDAO);

        userHandler = new UserHandler(userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
//        Spark.post("/user", (request, response) -> userHandler.registerHandler(request, response));
        Spark.delete("/db", this::clearHandler);
        Spark.post("/user", userHandler::registerHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clearHandler(Request request, Response response) throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        response.status(200);
        return "{}";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
