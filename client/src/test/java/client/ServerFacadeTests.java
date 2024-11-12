package client;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import requests.*;
import results.*;
import server.Server;

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
//        DatabaseManager.dropDatabase();
//        DatabaseManager.initializeDatabase();
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterEach
    void cleanup() throws DataAccessException {
        server.clearDatabase();
    }


    @Test
    public void registerSuccess() throws ResponseException {
        RegistrationRequest request = new RegistrationRequest("username", "password", "email");
        try {
            RegistrationResult result = facade.register(request);
            Assertions.assertEquals(result.username(), request.username());
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
