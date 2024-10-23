package unittest;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Requests.*;
import service.ResultExceptions;
import service.Results.CreateGameResult;
import service.Results.JoinGameResult;
import service.Results.LoginResult;
import service.Results.RegisterResult;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final UserService userService = new UserService();

    @BeforeEach
    void clear() throws ResultExceptions, DataAccessException {
        var emptyRequest = new EmptyRequest();
        userService.clear(emptyRequest);
    }

    @Test
    void registerSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var actual = userService.register(registerRequest);
        //how to test for any int?
        //assert not null authToken
        //var users = userService.listGames()
        var expected = new RegisterResult("lucyco7", "1");
        assertEquals(expected.username(), actual.username());
        assertNotNull(actual.authToken());
    }

    @Test
    void registerBadRequest() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException {
        var registerRequest = new RegisterRequest(null, "helloworld", "lucyco7@byu.edu");
        assertThrows(ResultExceptions.BadRequestError.class, () -> userService.register(registerRequest));
    }

    @Test
    void loginSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var actual = userService.login(loginRequest);
        var expected = new LoginResult("lucyco7", "1");
        assertEquals(expected.username(), actual.username());
        assertNotNull(actual.authToken());
    }

    @Test
    void loginAuthorizationError() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("wrongname", "helloworld");
        assertThrows(ResultExceptions.AuthorizationError.class, () -> userService.login(loginRequest));
    }

    @Test
    void logoutSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = userService.login(loginRequest);
        var logoutRequest = new LogoutRequest(loggedIn.authToken());
        var loggedOut = userService.logout(logoutRequest);
        //how to test for any int?
        //assert not null authToken
        var foundAuth = userService.memoryAuthDAO.getAuth(loggedIn.authToken());
        assertNull(foundAuth);
    }

    @Test
    void logoutAuthorizationError() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = userService.login(loginRequest);
        var logoutRequest = new LogoutRequest("1");
        assertThrows(ResultExceptions.AuthorizationError.class, () -> userService.logout(logoutRequest));
    }

    @Test
    void listGamesSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = userService.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = userService.createGame(createGameRequest1);
        var createGameRequest2 = new CreateGameRequest("Game 2", loggedIn.authToken());
        var createGame2 = userService.createGame(createGameRequest2);
        var listGamesRequest = new ListGamesRequest(loggedIn.authToken());
        var actual = userService.listGames(listGamesRequest);
        assertEquals(2, actual.games().size());
        assertTrue(actual.games().contains(userService.memoryGameDAO.getGame(createGame1.gameID())));
        assertTrue(actual.games().contains(userService.memoryGameDAO.getGame(createGame2.gameID())));
    }

    @Test
    void listAuthorizationError() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = userService.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = userService.createGame(createGameRequest1);
        var createGameRequest2 = new CreateGameRequest("Game 2", loggedIn.authToken());
        var createGame2 = userService.createGame(createGameRequest2);
        var listGamesRequest = new ListGamesRequest("1");
        assertThrows(ResultExceptions.AuthorizationError.class, () -> userService.listGames(listGamesRequest));
    }

    @Test
    void createGameSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = userService.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = userService.createGame(createGameRequest1);
        assertNotNull(createGame1.gameID());
    }

    @Test
    void createGameBadRequest() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = userService.login(loginRequest);
        assertThrows(ResultExceptions.BadRequestError.class, () -> userService.createGame(null));
    }

    @Test
    void joinGameSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = userService.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = userService.createGame(createGameRequest1);
        var joinGameRequest = new JoinGameRequest("BLACK", createGame1.gameID(), loggedIn.authToken());
        var actual = userService.joinGame(joinGameRequest);
        var expected = new JoinGameResult("BLACK", createGame1.gameID());
        assertEquals(expected, actual);
    }

    @Test
    void joinGameTakenError() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = userService.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = userService.createGame(createGameRequest1);
        var joinGameRequest = new JoinGameRequest("BLACK", createGame1.gameID(), loggedIn.authToken());
        var actual = userService.joinGame(joinGameRequest);
        var expected = new JoinGameResult("BLACK", createGame1.gameID());
        assertThrows(ResultExceptions.AlreadyTakenError.class, () -> userService.joinGame(joinGameRequest));
    }

    @Test
    void clearSuccess() throws ResultExceptions, ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException, ResultExceptions.AuthorizationError {
        var registerRequest = new RegisterRequest("lucyco7", "helloworld", "lucyco7@byu.edu");
        var registered = userService.register(registerRequest);
        var loginRequest = new LoginRequest("lucyco7", "helloworld");
        var loggedIn = userService.login(loginRequest);
        var createGameRequest1 = new CreateGameRequest("Game 1", loggedIn.authToken());
        var createGame1 = userService.createGame(createGameRequest1);
        var joinGameRequest = new JoinGameRequest("BLACK", createGame1.gameID(), loggedIn.authToken());
        var actual = userService.joinGame(joinGameRequest);
        var clearRequest = new EmptyRequest();
        userService.clear(clearRequest);
        assertNull(userService.memoryAuthDAO.getAuth(loggedIn.authToken()));
    }
}
