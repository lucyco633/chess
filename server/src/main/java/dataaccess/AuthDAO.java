package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws DataAccessException;

    String createAuth(String username) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}
