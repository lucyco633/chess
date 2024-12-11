package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import server.requests.*;
import server.results.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        //facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clear() throws ResponseException {
        EmptyRequest emptyRequest = new EmptyRequest();
        facade.clear(emptyRequest);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    void register() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult result = facade.register(request);
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    void registerSuccessTest() throws Exception {
        RegisterRequest request = new RegisterRequest("lucy", "lucy", "lucy");
        RegisterResult result = facade.register(request);
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    void login() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        LoginRequest request = new LoginRequest("player1", "password");
        LoginResult result = facade.login(request);
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    void loginFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        LoginRequest request = new LoginRequest("player1", "wrongpassword");
        assertThrows(ResponseException.class, () -> facade.login(request));
    }

    @Test
    void logout() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        EmptyResult loggedOut = facade.logout(logoutRequest);
        EmptyResult expected = new EmptyResult();
        assertEquals(loggedOut, expected);
    }

    @Test
    void logoutFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest("hello");
        assertThrows(ResponseException.class, () -> facade.logout(logoutRequest));
    }

    @Test
    void createGame() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game 1", registerResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        assertNotNull(createGameResult.gameID());
    }

    @Test
    void createGameFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game 1", "badauth");
        assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest));
    }

    @Test
    void listGames() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game 1", registerResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        CreateGameRequest createGameRequest2 = new CreateGameRequest("game 2", registerResult.authToken());
        CreateGameResult createGameResult2 = facade.createGame(createGameRequest2);
        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
        Collection<GameData> listOfGames = listGamesResult.games();
        assertEquals(listOfGames.size(), 2);
    }

    @Test
    void listGamesFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game 1", registerResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        CreateGameRequest createGameRequest2 = new CreateGameRequest("game 2", registerResult.authToken());
        CreateGameResult createGameResult2 = facade.createGame(createGameRequest2);
        ListGamesRequest listGamesRequest = new ListGamesRequest("badauth");
        assertThrows(ResponseException.class, () -> facade.listGames(listGamesRequest));
    }

    @Test
    void joinGame() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game 1", registerResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", createGameResult.gameID(),
                registerResult.authToken());
        JoinGameResult joinGameResult = facade.joinGame(joinGameRequest);
        assertEquals(joinGameResult.playerColor(), "BLACK");
    }

    @Test
    void joinGameFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game 1", registerResult.authToken());
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", createGameResult.gameID(),
                "badauth");
        assertThrows(ResponseException.class, () -> facade.joinGame(joinGameRequest));
    }

}
