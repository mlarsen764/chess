package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private Map<String, UserData> userMap = new HashMap<>();
    @Override
    public void clear() {
        userMap.clear();
    }
    @Override
    public void createUser(UserData u) throws DataAccessException {
        if (userMap.containsKey(u.username())) {
            throw new DataAccessException("User already exists");
        }
        userMap.put(u.username(), u);
    }
    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = userMap.get(username);
        if (user == null) {
            throw new DataAccessException("User not found");
        }
        return user;
    }
}
