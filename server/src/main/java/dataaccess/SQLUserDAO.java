package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO {
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        String sql = ""
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}
