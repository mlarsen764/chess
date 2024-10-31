package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTest {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private UserData testUser;
    private GameData testGame;
    private GameData testGame2;

    @BeforeEach
    void setUp() throws DataAccessException, SQLException {
        DatabaseManager.dropDatabase("chess");
        DatabaseManager.initializeDatabase();
        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        testUser = new UserData("testUser", "password123", "email@email.com");
        testGame = new GameData(1111, null, null, "Test", new ChessGame());
        testGame2 = new GameData(2, null, null, "Test", new ChessGame());
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        gameDAO.createGame(testGame);
        assertNotNull(gameDAO.getGame(testGame.gameID()));
        gameDAO.clear();
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(testGame.gameID()));
    }

    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        gameDAO.createGame(testGame);
        GameData retrievedGame = gameDAO.getGame(testGame.gameID());
        assertNotNull(retrievedGame);
        assertEquals(testGame.gameID(), retrievedGame.gameID());
        assertEquals(testGame.gameName(), retrievedGame.gameName());
    }

    @Test
    public void testCreateGameFail() throws DataAccessException {
        gameDAO.createGame(testGame);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(testGame));
    }

    @Test
    public void testGetGameSuccess() throws DataAccessException {
        gameDAO.createGame(testGame);
        GameData retrievedGame = gameDAO.getGame(testGame.gameID());
        assertNotNull(retrievedGame);
        assertEquals(testGame.gameID(), retrievedGame.gameID());
        assertEquals(testGame.whiteUsername(), retrievedGame.whiteUsername());
    }

    @Test
    public void testGetGameFail() {
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(555));
    }

    @Test
    public void testListGamesSuccess() throws DataAccessException {
        gameDAO.createGame(testGame);
        gameDAO.createGame(testGame2);
        Collection<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesFail() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }


}
