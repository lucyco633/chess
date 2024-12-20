package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ResultExceptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SqlTests {

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
    void clear() throws ResultExceptions, DataAccessException, SQLException {
        SQL_AUTH_DAO.deleteAllAuth();
        SQL_GAME_DAO.deleteAllGames();
        SQL_USER_DAO.deleteAllUsers();
    }

    @Test
    void getAuthSuccess() throws ResultExceptions, DataAccessException, SQLException {
        String authToken = SQL_AUTH_DAO.createAuth("lucyco7");
        AuthData authData = SQL_AUTH_DAO.getAuth(authToken);
        assertNotNull(authData);
    }

    @Test
    void getAuthFail() throws ResultExceptions, DataAccessException, SQLException {
        AuthData authData = SQL_AUTH_DAO.getAuth("hello");
        assertNull(authData);
    }

    @Test
    void createAuthSuccess() throws ResultExceptions, DataAccessException, SQLException {
        String authToken = SQL_AUTH_DAO.createAuth("lucyco7");
        AuthData authData = SQL_AUTH_DAO.getAuth(authToken);
        assertNotNull(authData);
    }

    @Test
    void deleteAuthSuccess() throws ResultExceptions, DataAccessException, SQLException {
        String authToken = SQL_AUTH_DAO.createAuth("lucyco7");
        SQL_AUTH_DAO.deleteAuth(authToken);
        assertNull(SQL_AUTH_DAO.getAuth(authToken));
    }

    @Test
    void deleteAuthFail() throws ResultExceptions, DataAccessException, SQLException {
        String authToken = SQL_AUTH_DAO.createAuth("lucyco7");
        SQL_AUTH_DAO.deleteAuth("hello");
        assertNotNull(SQL_AUTH_DAO.getAuth(authToken));
    }

    @Test
    void deleteAllAuthSuccess() throws ResultExceptions, DataAccessException, SQLException {
        String authToken = SQL_AUTH_DAO.createAuth("lucyco7");
        SQL_AUTH_DAO.deleteAllAuth();
        assertNull(SQL_AUTH_DAO.getAuth(authToken));
    }

    @Test
    void getUserSuccess() throws ResultExceptions, DataAccessException, SQLException {
        SQL_USER_DAO.createUser("lucyco7", "hello", "lucyco7@byu.edu");
        UserData userData = SQL_USER_DAO.getUser("lucyco7");
        assertNotNull(userData);
    }

    @Test
    void getUserFail() throws ResultExceptions, DataAccessException, SQLException {
        SQL_USER_DAO.createUser("lucyco7", "hello", "lucyco7@byu.edu");
        UserData userData = SQL_USER_DAO.getUser("lucy");
        assertNull(userData);
    }

    @Test
    void createUserSuccess() throws ResultExceptions, DataAccessException, SQLException {
        SQL_USER_DAO.createUser("lucyco7", "hello", "lucyco7@byu.edu");
        UserData userData = SQL_USER_DAO.getUser("lucyco7");
        assertNotNull(userData);
    }

    @Test
    void deleteAllUsersSuccess() throws ResultExceptions, DataAccessException, SQLException {
        SQL_USER_DAO.createUser("lucyco7", "hello", "lucyco7@byu.edu");
        SQL_USER_DAO.deleteAllUsers();
        UserData userData = SQL_USER_DAO.getUser("lucyco7");
        assertNull(userData);
    }

    @Test
    void createGameSuccess() throws ResultExceptions, DataAccessException, SQLException {
        GameData gameData = SQL_GAME_DAO.createGame("Game 1");
        assertNotNull(gameData);
    }

    @Test
    void getGameSuccess() throws ResultExceptions, DataAccessException, SQLException {
        GameData newGame = SQL_GAME_DAO.createGame("Game 1");
        GameData gameData = SQL_GAME_DAO.getGame(newGame.gameID());
        assertNotNull(gameData);
    }

    @Test
    void getGameFail() throws ResultExceptions, DataAccessException, SQLException {
        GameData newGame = SQL_GAME_DAO.createGame("Game 1");
        GameData gameData = SQL_GAME_DAO.getGame(1);
        assertNull(gameData);
    }

    @Test
    void updateGameSuccess() throws ResultExceptions, DataAccessException, SQLException {
        GameData newGame = SQL_GAME_DAO.createGame("Game 1");
        SQL_GAME_DAO.updateGame(newGame.gameID(), "lucyco7", "hemmee", newGame.gameName(), newGame.game(), false);
        GameData actualGame = SQL_GAME_DAO.getGame(newGame.gameID());
        GameData expectedGame = new GameData(newGame.gameID(), "lucyco7", "hemmee", newGame.gameName(), newGame.game(), newGame.resigned());
        assertEquals(actualGame.whiteUsername(), expectedGame.whiteUsername());
    }

    @Test
    void updateGameFail() throws ResultExceptions, DataAccessException, SQLException {
        GameData newGame = SQL_GAME_DAO.createGame("Game 1");
        SQL_GAME_DAO.updateGame(newGame.gameID(), "lucyco7", "hemmee", newGame.gameName(), newGame.game(), false);
        GameData actualGame = SQL_GAME_DAO.getGame(newGame.gameID());
        GameData expectedGame = new GameData(newGame.gameID(), "lucyco", "henry", newGame.gameName(), newGame.game(), newGame.resigned());
        assertNotEquals(actualGame.whiteUsername(), expectedGame.whiteUsername());
    }

    @Test
    void listGameSuccess() throws ResultExceptions, DataAccessException, SQLException {
        GameData newGame = SQL_GAME_DAO.createGame("Game 1");
        GameData newGame2 = SQL_GAME_DAO.createGame("Game 2");
        Collection<GameData> listGamesActual = SQL_GAME_DAO.listGames();
        Collection<GameData> listGamesExpected = new ArrayList<>();
        listGamesExpected.add(newGame);
        listGamesExpected.add(newGame2);
        assertEquals(listGamesExpected, listGamesActual);
    }

    @Test
    void listGameFail() throws ResultExceptions, DataAccessException, SQLException {
        GameData newGame = SQL_GAME_DAO.createGame("Game 1");
        GameData newGame2 = SQL_GAME_DAO.createGame("Game 2");
        SQL_GAME_DAO.deleteAllGames();
        Collection<GameData> listGamesActual = SQL_GAME_DAO.listGames();
        Collection<GameData> listGamesExpected = new ArrayList<>();
        listGamesExpected.add(newGame);
        listGamesExpected.add(newGame2);
        assertNotEquals(listGamesExpected, listGamesActual);
    }

    @Test
    void deleteAllGamesSuccess() throws ResultExceptions, DataAccessException, SQLException {
        GameData newGame = SQL_GAME_DAO.createGame("Game 1");
        GameData newGame2 = SQL_GAME_DAO.createGame("Game 2");
        SQL_GAME_DAO.deleteAllGames();
        GameData findGame = SQL_GAME_DAO.getGame(newGame.gameID());
        GameData findGame2 = SQL_GAME_DAO.getGame(newGame2.gameID());
        assertNull(findGame);
        assertNull(findGame2);
    }
}
