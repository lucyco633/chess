package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.requests.*;
import service.results.JoinGameResult;
import service.results.LoginResult;
import service.results.RegisterResult;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final Service USER_SERVICE;

    static {
        try {
            USER_SERVICE = new Service();
        } catch (ResultExceptions e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws ResultExceptions, DataAccessException, SQLException {
        var emptyRequest = new EmptyRequest();
        USER_SERVICE.clear(emptyRequest);
    }

    @Test
    void registerSuccess() throws ResultExceptions, ResultExceptions.BadRequestError,
            ResultExceptions.AlreadyTakenError, DataAccessException, SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var actual = USER_SERVICE.register(registerRequest);
        var expected = new RegisterResult("lucyco7", "1");
        assertEquals(expected.username(), actual.username());
        assertNotNull(actual.authToken());
    }

    @Test
    void registerBadRequest() {
        var registerRequest = new RegisterRequest(null, "helloworld", "lucyco7@byu.edu");
        assertThrows(ResultExceptions.BadRequestError.class, () -> USER_SERVICE.register(registerRequest));
    }

    @Test
    void loginSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError,
            DataAccessException, ResultExceptions.AuthorizationError, SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var actual = USER_SERVICE.login(loginRequest);
        var expected = new LoginResult("lucyco7", "1");
        assertEquals(expected.username(), actual.username());
        assertNotNull(actual.authToken());
    }

    @Test
    void loginAuthorizationError() throws ResultExceptions, ResultExceptions.BadRequestError,
            ResultExceptions.AlreadyTakenError, DataAccessException, SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("wrongname", "helloworld");
        assertThrows(ResultExceptions.AuthorizationError.class, () -> USER_SERVICE.login(loginRequest));
    }

    @Test
    void logoutSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError,
            DataAccessException, ResultExceptions.AuthorizationError, SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = USER_SERVICE.login(loginRequest);
        var logoutRequest = new LogoutRequest(loggedIn.authToken());
        var loggedOut = USER_SERVICE.logout(logoutRequest);
        var foundAuth = USER_SERVICE.sqlAuthDAO.getAuth(loggedIn.authToken());
        assertNull(foundAuth);
    }

    @Test
    void logoutAuthorizationError() throws ResultExceptions, ResultExceptions.BadRequestError,
            ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError, SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = USER_SERVICE.login(loginRequest);
        var logoutRequest = new LogoutRequest("1");
        assertThrows(ResultExceptions.AuthorizationError.class, () -> USER_SERVICE.logout(logoutRequest));
    }

    @Test
    void listGamesSuccess() throws ResultExceptions, ResultExceptions.BadRequestError,
            ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError,
            SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = USER_SERVICE.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = USER_SERVICE.createGame(createGameRequest1);
        var createGameRequest2 = new CreateGameRequest("Game 2", loggedIn.authToken());
        var createGame2 = USER_SERVICE.createGame(createGameRequest2);
        var listGamesRequest = new ListGamesRequest(loggedIn.authToken());
        var actual = USER_SERVICE.listGames(listGamesRequest);
        assertEquals(2, actual.games().size());
        assertTrue(actual.games().contains(USER_SERVICE.sqlGameDAO.getGame(createGame1.gameID())));
        assertTrue(actual.games().contains(USER_SERVICE.sqlGameDAO.getGame(createGame2.gameID())));
    }

    @Test
    void listAuthorizationError() throws ResultExceptions, ResultExceptions.BadRequestError,
            ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError,
            SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = USER_SERVICE.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = USER_SERVICE.createGame(createGameRequest1);
        var createGameRequest2 = new CreateGameRequest("Game 2", loggedIn.authToken());
        var createGame2 = USER_SERVICE.createGame(createGameRequest2);
        var listGamesRequest = new ListGamesRequest("1");
        assertThrows(ResultExceptions.AuthorizationError.class, () -> USER_SERVICE.listGames(listGamesRequest));
    }

    @Test
    void createGameSuccess() throws ResultExceptions, ResultExceptions.BadRequestError,
            ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError,
            SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = USER_SERVICE.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = USER_SERVICE.createGame(createGameRequest1);
        assertNotNull(createGame1.gameID());
    }

    @Test
    void createGameBadRequest() throws ResultExceptions, ResultExceptions.BadRequestError,
            ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError, SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = USER_SERVICE.login(loginRequest);
        assertThrows(ResultExceptions.BadRequestError.class, () -> USER_SERVICE.createGame(null));
    }

    @Test
    void joinGameSuccess() throws ResultExceptions, ResultExceptions.BadRequestError,
            ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError,
            SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = USER_SERVICE.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = USER_SERVICE.createGame(createGameRequest1);
        var joinGameRequest = new JoinGameRequest("BLACK", createGame1.gameID(), loggedIn.authToken());
        var actual = USER_SERVICE.joinGame(joinGameRequest);
        var expected = new JoinGameResult("BLACK", createGame1.gameID());
        assertEquals(expected, actual);
    }

    @Test
    void joinGameTakenError() throws ResultExceptions, ResultExceptions.BadRequestError,
            ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError,
            SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = USER_SERVICE.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = USER_SERVICE.createGame(createGameRequest1);
        var joinGameRequest = new JoinGameRequest("BLACK", createGame1.gameID(), loggedIn.authToken());
        var actual = USER_SERVICE.joinGame(joinGameRequest);
        var expected = new JoinGameResult("BLACK", createGame1.gameID());
        assertThrows(ResultExceptions.AlreadyTakenError.class, () -> USER_SERVICE.joinGame(joinGameRequest));
    }

    @Test
    void clearSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError,
            DataAccessException, ResultExceptions.AuthorizationError, SQLException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = USER_SERVICE.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = USER_SERVICE.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = USER_SERVICE.createGame(createGameRequest1);
        var joinGameRequest = new JoinGameRequest("BLACK", createGame1.gameID(), loggedIn.authToken());
        var actual = USER_SERVICE.joinGame(joinGameRequest);
        var clearRequest = new EmptyRequest();
        USER_SERVICE.clear(clearRequest);
        assertNull(USER_SERVICE.sqlAuthDAO.getAuth(loggedIn.authToken()));
    }
}
