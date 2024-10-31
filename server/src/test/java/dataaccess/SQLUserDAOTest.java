package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTest {

    private SQLUserDAO userDAO;
    private UserData testUser;

    @BeforeEach
    void setUp() throws DataAccessException, SQLException {
        DatabaseManager.dropDatabase("chess");
        DatabaseManager.initializeDatabase();
        userDAO = new SQLUserDAO();
        testUser = new UserData("testUser", "password123", "email@email.com");
    }



    @Test
    public void testCreateUserSuccess() throws DataAccessException {
//        DatabaseManager.printUserTableColumns();
        userDAO.createUser(testUser);

        UserData retrievedUser = userDAO.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.username());
        assertNotEquals("password123", retrievedUser.password());
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
