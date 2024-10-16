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
        AuthData auth = authMap.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Authorization not found");
        }
        return auth;
    }
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authMap.containsKey(authToken)) {
            throw new DataAccessException("Authorization not found");
        }
        authMap.remove(authToken);
    }
}
