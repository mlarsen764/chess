package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTest {

    private SQLUserDAO userDAO;
    private UserData testUser;

    @BeforeEach
    void setUp() throws DataAccessException, SQLException {
        DatabaseManager.dropDatabase();
        DatabaseManager.initializeDatabase();
        userDAO = new SQLUserDAO();
        testUser = new UserData("testUser", "password123", "email@email.com");
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        if (userDAO != null) {
            userDAO.clear();
        }
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        userDAO.createUser(testUser);
        userDAO.clear();
        assertThrows(DataAccessException.class, () -> userDAO.getUser("testUser"));
    }

    @Test
    public void testCreateUserSuccess() throws DataAccessException {
        userDAO.createUser(testUser);
        UserData retrievedUser = userDAO.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.username());
        assertNotEquals("password123", retrievedUser.password());
    }

    @Test
    public void testCreateUserFail() throws DataAccessException {
        userDAO.createUser(testUser);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(testUser));
    }

    @Test
    public void testGetUserSuccess() throws DataAccessException {
        userDAO.createUser(testUser);
        UserData retrievedUser = userDAO.getUser("testUser");
        assertEquals(testUser.username(), retrievedUser.username());
        assertEquals(testUser.email(), retrievedUser.email());
    }

    @Test
    public void testGetUserFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> userDAO.getUser("falseUser"));
    }

    @Test
    public void testVerifyUserSuccess() throws DataAccessException {
        userDAO.createUser(testUser);
        boolean isVerified = userDAO.verifyUser("testUser", "password123");
        assertTrue(isVerified);
    }

    @Test
    public void testVerifyUserFail() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "test@example.com");
        userDAO.createUser(user);
        boolean isVerified = userDAO.verifyUser("testUser", "wrongPassword");
        assertFalse(isVerified);
    }
}
