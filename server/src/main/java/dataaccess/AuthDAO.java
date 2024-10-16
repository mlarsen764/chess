package dataaccess;

import model.AuthData;

public interface AuthDAO {
//    createAuth: Create a new authorization.
//    getAuth: Retrieve an authorization given an authToken.
//    deleteAuth: Delete an authorization so that it is no longer valid.
    void clear() throws DataAccessException;
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

}
