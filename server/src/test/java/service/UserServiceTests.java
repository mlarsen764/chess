package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    UserData mockUser;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
        mockUser = new UserData("Matt", "test123", "test@test.test");
    }

    @Test
    public void testCreateAndGetUser() throws DataAccessException {
        userDAO.createUser(mockUser);
        UserData retrievedUser = userDAO.getUser("Matt");
        assertEquals(mockUser.username(), retrievedUser.username());
    }

    @Test
    public void testClearSuccess() throws DataAccessException {
        userDAO.createUser(mockUser);
        userDAO.clear();
        assertThrows(DataAccessException.class, () -> userDAO.getUser("jimbo"));
    }

    @Test
    public void testRegistrationSuccess() throws DataAccessException {
        AuthData resultAuth = userService.register(mockUser);
        assertEquals(authDAO.getAuth(resultAuth.authToken()), resultAuth);
    }

    @Test
    public void testRegistrationFail() throws DataAccessException {
        userDAO.createUser(mockUser);

        DataAccessException thrown = assertThrows(
                DataAccessException.class,
                () -> userService.register(mockUser),
                "Expected register() to say User already exists"
        );
        assertEquals("User already exists", thrown.getMessage());
    }

    @Test
    public void testLoginSuccess() throws DataAccessException {
        userDAO.createUser(mockUser);
        AuthData resultAuth = userService.login(mockUser);
        assertEquals(authDAO.getAuth(resultAuth.authToken()), resultAuth);
    }

    @Test
    public void testLoginFail() throws DataAccessException {
        userDAO.createUser(mockUser);
        UserData falseUser = new UserData("Matt", "321", "test@test.test");
        DataAccessException thrown = assertThrows(
                DataAccessException.class,
                () -> userService.login(falseUser),
                "Expected login() to report Invalid username or password"
        );
        assertEquals("Invalid username or password", thrown.getMessage());
    }

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        userDAO.createUser(mockUser);
        AuthData authData = userService.login(mockUser);
        userService.logout(authData);
        assertThrows(DataAccessException.class, () -> userService.logout(authData));
    }

    @Test
    public void testLogoutFail() throws DataAccessException {
        AuthData invalidData = new AuthData("invalidToken", "noUser");
        assertThrows(DataAccessException.class, () -> userService.logout(invalidData));
    }

}
