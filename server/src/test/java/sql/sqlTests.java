package sql;

import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import dataaccess.SqlUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ResultExceptions;
import service.requests.EmptyRequest;

import static org.junit.jupiter.api.Assertions.*;

public class sqlTests {

    static final SqlAuthDAO SQL_AUTH_DAO;
    static final SqlGameDAO SQL_GAME_DAO;
    static final SqlUserDAO SQL_USER_DAO;

    static {
        try {
            SQL_USER_DAO = new SqlUserDAO();
        } catch (ResultExceptions e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            SQL_AUTH_DAO = new SqlAuthDAO();
        } catch (ResultExceptions e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            SQL_GAME_DAO = new SqlGameDAO();
        } catch (ResultExceptions e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws ResultExceptions, DataAccessException {
        SQL_AUTH_DAO.deleteAllAuth();
        SQL_GAME_DAO.deleteAllGames();
        SQL_USER_DAO.deleteAllUsers();
    }

    @Test
    void getAuthSuccess() throws ResultExceptions, DataAccessException {
        String authToken = SQL_AUTH_DAO.createAuth("lucyco7");
        AuthData authData = SQL_AUTH_DAO.getAuth(authToken);
        assertNotNull(authData);
    }

    @Test
    void getAuthFail() throws ResultExceptions, DataAccessException {
        AuthData authData = SQL_AUTH_DAO.getAuth("hello");
        assertNull(authData);
    }

    @Test
    void createAuthSuccess() throws ResultExceptions, DataAccessException {
        String authToken = SQL_AUTH_DAO.createAuth("lucyco7");
        AuthData authData = SQL_AUTH_DAO.getAuth(authToken);
        assertNotNull(authData);
    }

    @Test
    void createAuthFail() throws ResultExceptions, DataAccessException {
        SQL_AUTH_DAO.createAuth("lucyco7");
        assertThrows(ResultExceptions.class, () -> SQL_AUTH_DAO.createAuth("lucyco7"));
    }

    @Test
    void deleteAuthSuccess() throws ResultExceptions, DataAccessException {
        String authToken = SQL_AUTH_DAO.createAuth("lucyco7");
        SQL_AUTH_DAO.deleteAuth(authToken);
        assertNull(SQL_AUTH_DAO.getAuth(authToken));
    }

    @Test
    void getUserSuccess() throws ResultExceptions, DataAccessException {
        SQL_USER_DAO.createUser("lucyco7", "hello", "lucyco7@byu.edu");
        UserData userData = SQL_USER_DAO.getUser("lucyco7");
        assertNotNull(userData);
    }

    @Test
    void getUserFail() throws ResultExceptions, DataAccessException {
        SQL_USER_DAO.createUser("lucyco7", "hello", "lucyco7@byu.edu");
        UserData userData = SQL_USER_DAO.getUser("lucy");
        assertNull(userData);
    }

    @Test
    void createUserSuccess() throws ResultExceptions, DataAccessException {
        SQL_USER_DAO.createUser("lucyco7", "hello", "lucyco7@byu.edu");
        UserData userData = SQL_USER_DAO.getUser("lucyco7");
        assertNotNull(userData);
    }

    @Test
    void createUserFail() throws ResultExceptions, DataAccessException {
        SQL_USER_DAO.createUser("lucyco7", "hello", "lucyco7@byu.edu");
        UserData userData = SQL_USER_DAO.getUser("lucy");
        //assertThrows();
    }

    @Test
    void createGameSuccess() throws ResultExceptions, DataAccessException {
        GameData gameData = SQL_GAME_DAO.createGame("Game 1");
        assertNotNull(gameData);
    }

    @Test
    void createGameFail() throws ResultExceptions, DataAccessException {
        SQL_USER_DAO.createUser("lucyco7", "hello", "lucyco7@byu.edu");
        UserData userData = SQL_USER_DAO.getUser("lucy");
        //assertThrows();
    }
}
