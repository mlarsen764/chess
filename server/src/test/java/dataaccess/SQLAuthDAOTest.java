package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTest {

    private AuthDAO authDAO;
    private UserDAO userDAO;
    private AuthData testAuth;
    private UserData testUser;

    @BeforeEach
    void setUp() throws DataAccessException, SQLException {
        DatabaseManager.dropDatabase("chess");
        DatabaseManager.initializeDatabase();
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        testUser = new UserData("testUser", "password123", "email@email.com");
        testAuth = new AuthData("token", "username");
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        if (authDAO != null) {
            authDAO.clear();
        }
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        authDAO.createAuth(testAuth);
        authDAO.clear();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    public void testCreateAuthSuccess() throws DataAccessException {
        authDAO.createAuth(testAuth);
        AuthData retrievedAuth = authDAO.getAuth(testAuth.authToken());
        assertNotNull(retrievedAuth);
        assertEquals(testAuth.authToken(), retrievedAuth.authToken());
    }

    @Test
    public void testCreateAuthFail() throws DataAccessException {
        authDAO.createAuth(testAuth);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(testAuth));
    }

    @Test
    public void testGetAuthSuccess() throws DataAccessException {
        authDAO.createAuth(testAuth);
        AuthData retrievedAuth = authDAO.getAuth(testAuth.authToken());
        assertEquals(testAuth.authToken(), retrievedAuth.authToken());
    }

    @Test
    public void testGetAuthFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("falseToken"));
    }

    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {
        authDAO.createAuth(testAuth);
        authDAO.deleteAuth(testAuth.authToken());
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("token"));
    }

    @Test
    public void testDeleteAuthFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("falseToken"));
    }
}
