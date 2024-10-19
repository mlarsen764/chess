package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private Map<String, AuthData> authMap = new HashMap<>();
    @Override
    public void clear() {
        authMap.clear();
    }
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        if (authMap.containsKey(auth.authToken())) {
            throw new DataAccessException("Authorization already exists");
        }
        authMap.put(auth.authToken(), auth);
    }
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authMap.containsKey(authToken)) {
            throw new DataAccessException("Unauthorized");
        }
        return authMap.get(authToken);
    }
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authMap.containsKey(authToken)) {
            throw new DataAccessException("Unauthorized");
        }
        authMap.remove(authToken);
    }
}
