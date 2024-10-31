package server;

import dataaccess.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    UserService userService;
    GameService gameService;

    UserHandler userHandler;
    GameHandler gameHandler;

    public Server() {
        try {
            DatabaseManager.initializeDatabase();
        } catch (DataAccessException e) {
            System.err.println("Failed to initialize the database: " + e.getMessage());
            System.exit(1);
        }

//        userDAO = new MemoryUserDAO();
//        authDAO = new MemoryAuthDAO();
//        gameDAO = new MemoryGameDAO();

          userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
//        Spark.post("/user", (request, response) -> userHandler.registerHandler(request, response));
        Spark.delete("/db", this::clearHandler);
        Spark.post("/user", userHandler::registerHandler);
        Spark.post("/session", userHandler::loginHandler);
        Spark.delete("/session", userHandler::logoutHandler);
        Spark.get("/game", gameHandler::listGamesHandler);
        Spark.post("/game", gameHandler::createGameHandler);
        Spark.put("/game", gameHandler::joinGameHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object clearHandler(Request request, Response response) throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
        response.status(200);
        return "{}";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
