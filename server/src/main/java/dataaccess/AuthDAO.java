package dataaccess;

import model.AuthData;
import service.ResultExceptions;

import java.sql.SQLException;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws DataAccessException, ResultExceptions, SQLException;

    String createAuth(String username) throws DataAccessException, ResultExceptions, SQLException;

    void deleteAuth(String authToken) throws DataAccessException, ResultExceptions, SQLException;
}
