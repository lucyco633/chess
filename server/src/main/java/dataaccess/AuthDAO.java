package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws DataAccessException;

    String createAuth(String username) throws DataAccessException;

    void updateAuth(AuthData authData, String username);

    void deleteAuth(String authToken) throws DataAccessException;
}
