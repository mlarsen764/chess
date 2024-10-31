package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.*;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    public AuthData register(UserData user) throws DataAccessException {
        try {
            if (userDAO.getUser(user.username()) != null) {
                throw new DataAccessException(("User already exists"));
            }
        } catch (DataAccessException e) {
            if (!e.getMessage().equals("User not found")) {
                throw e;
            }
        }
        userDAO.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public AuthData login(UserData user) throws DataAccessException {
        UserData existingUser = userDAO.getUser(user.username());
        if (!userDAO.verifyUser(user.username(), user.password())) {
            throw new DataAccessException("Invalid username or password");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("Authorization not found");
        }
        authDAO.deleteAuth(authToken);
    }

    public void clearUsers() throws DataAccessException {
        userDAO.clear();
    }
}
