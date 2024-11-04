package dataaccess;

import model.AuthData;
import service.ResultExceptions;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws DataAccessException, ResultExceptions;

    String createAuth(String username) throws DataAccessException, ResultExceptions;

    void deleteAuth(String authToken) throws DataAccessException, ResultExceptions;
}
