package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameDAO gameDAO;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    private GameService gameService;
    private UserService userService;

    GameData mockGame;
    UserData mockUser;
    UserData mockUser2;
    AuthData mockAuth;
    AuthData mockAuth2;

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        mockUser = new UserData("Matt", "test123", "test@test.test");
        mockUser2 = new UserData("Ken", "test123", "test@test.com");
        mockGame = new GameData(1111, null, null, "Test", new ChessGame());
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
        mockAuth = userService.register(mockUser);
        mockAuth2 = userService.register(mockUser2);
    }

    @Test
    public void testListGamesSuccess() throws DataAccessException {
        gameDAO.createGame(mockGame);
        Collection<GameData> games = gameService.listGames(mockAuth.authToken());
        assertEquals(1, games.size());
        assertTrue(games.contains(mockGame));
    }

    @Test
    public void testListGamesFail() throws DataAccessException {
        gameDAO.createGame(mockGame);
        DataAccessException thrown = assertThrows(
                DataAccessException.class,
                () -> gameService.listGames("invalidToken"),
                "Expected listGames to report Unauthorized"
        );
        assertEquals("Unauthorized", thrown.getMessage());
    }

    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        GameData createdGame = gameService.createGame(mockAuth.authToken(), mockGame);

        assertEquals(mockGame.gameID(), createdGame.gameID());
        assertNotNull(gameDAO.getGame(createdGame.gameID()));
    }

    @Test
    public void testCreateGameFail() throws DataAccessException {
        DataAccessException thrown = assertThrows(
                DataAccessException.class,
                () -> gameService.createGame("invalidToken", mockGame),
                "Expected createGame() to report Unauthorized"
        );
        assertEquals("Unauthorized", thrown.getMessage());
    }

    @Test
    public void testJoinGameSuccess() throws DataAccessException {
        gameService.createGame(mockAuth.authToken(), mockGame);
        gameService.joinGame(mockAuth.authToken(), mockGame.gameID(), "WHITE", mockGame.game());

        GameData updatedGame = gameDAO.getGame(mockGame.gameID());
        assertEquals(mockUser.username(), updatedGame.whiteUsername());
    }

    @Test
    public void testJoinGameFail() throws DataAccessException {
        gameDAO.createGame(mockGame);
        gameService.joinGame(mockAuth.authToken(), mockGame.gameID(), "WHITE", mockGame.game());

        DataAccessException thrown = assertThrows(
                DataAccessException.class,
                () -> gameService.joinGame(mockAuth2.authToken(), mockGame.gameID(), "WHITE", mockGame.game()),
                "Expected joinGame to report Already taken"
        );
        assertEquals("Already taken", thrown.getMessage());
    }
}