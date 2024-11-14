package client;

import dataaccess.DataAccessException;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import requests.*;
import results.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private ServerFacade facade;
    static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setup() throws Exception {
        server.clearDatabase();
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterEach
    void cleanup() throws DataAccessException {
        server.clearDatabase();
        facade = new ServerFacade("http://localhost:" + port);
    }


    @Test
    public void registerSuccess() throws ResponseException {
        RegistrationRequest request = new RegistrationRequest("user", "pass", "email");
        RegistrationResult result = facade.register(request);
        Assertions.assertEquals(result.username(), request.username());
    }

    @Test
    public void registerFail() throws ResponseException {
        RegistrationRequest request = new RegistrationRequest("user", "pass", "email");
        facade.register(request);
        assertThrows(ResponseException.class, () -> facade.register(request));
    }

    @Test
    public void loginSuccess() throws ResponseException {
        RegistrationRequest registrationRequest = new RegistrationRequest("user", "pass", "email");
        facade.register(registrationRequest);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        LoginResult loginResult = facade.login(loginRequest);
        Assertions.assertEquals(registrationRequest.username(), loginResult.username());
    }

    @Test
    public void loginFail() throws ResponseException {
        LoginRequest badRequest = new LoginRequest("fake", "pass");
        assertThrows(ResponseException.class, () -> facade.login(badRequest));
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        RegistrationRequest registerRequest = new RegistrationRequest("user", "pass", "email");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        LoginResult loginResult = facade.login(loginRequest);

        facade.logout(loginResult);
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        RegistrationRequest registerRequest = new RegistrationRequest("user", "pass", "email");
        facade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("user", "pass");
        LoginResult loginResult = facade.login(loginRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("token", "game");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, loginResult);
        assertNotNull(createGameResult);
        assertTrue(createGameResult.gameID() > 0);
    }

    @Test
    public void createGameFail() throws ResponseException {
        CreateGameRequest badRequest = new CreateGameRequest("fake", "nope");
        assertThrows(ResponseException.class, () -> facade.createGame(badRequest, null));
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        RegistrationRequest registerRequest = new RegistrationRequest("user", "pass", "email");
        facade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("user", "pass");
        LoginResult loginResult = facade.login(loginRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "game");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, loginResult);
        JoinGameRequest joinRequest = new JoinGameRequest(loginResult.authToken(), "WHITE", createGameResult.gameID());
        JoinGameResult joinResult = facade.joinGame(joinRequest, loginResult);
        assertNotNull(joinResult);
    }

    @Test
    public void joinGameFail() throws ResponseException {
        JoinGameRequest badRequest = new JoinGameRequest("none", "WHITE", 5);
        assertThrows(ResponseException.class, () -> facade.joinGame(badRequest, null));
    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        RegistrationRequest registerRequest = new RegistrationRequest("user", "pass", "email");
        facade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("user", "pass");
        LoginResult loginResult = facade.login(loginRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "game");
        facade.createGame(createGameRequest, loginResult);

        CreateGameRequest gameRequest2 = new CreateGameRequest(loginResult.authToken(), "game2");
        facade.createGame(gameRequest2, loginResult);

        ListGamesRequest listRequest = new ListGamesRequest(loginResult.authToken());
        ListGamesResult listResult = facade.listGames(listRequest, loginResult);
        Assertions.assertEquals(listResult.games().size(), 2);
    }

    @Test
    public void listGamesFail() throws ResponseException {
        RegistrationRequest registerRequest = new RegistrationRequest("user", "pass", "email");
        facade.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("user", "pass");
        LoginResult loginResult = facade.login(loginRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), "game");
        facade.createGame(createGameRequest, loginResult);

        ListGamesRequest listRequest = new ListGamesRequest(loginResult.authToken());
        ListGamesResult listResult = facade.listGames(listRequest, loginResult);
        Assertions.assertNotEquals(listResult.games().size(), 2);
    }

}
