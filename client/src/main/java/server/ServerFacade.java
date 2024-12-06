package server;

import chess.ChessMove;
import com.google.gson.Gson;
import server.requests.*;
import server.results.*;
import ui.ErrorMessages;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;
    private WebSocketCommunicator webSocketCommunicator;

    public ServerFacade(String url) throws ResponseException {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class, null);
    }

    public EmptyResult logout(LogoutRequest request) throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, request, EmptyResult.class, request.authToken());
    }

    public ListGamesResult listGames(ListGamesRequest request) throws ResponseException {
        var path = "/game";
        return this.makeRequestListGames("GET", path, request, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, request, CreateGameResult.class, request.authToken());
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws ResponseException, IOException {
        var path = "/game";
        var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                request.authToken(), request.gameID());
        webSocketCommunicator.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        return this.makeRequest("PUT", path, request, JoinGameResult.class, request.authToken());
    }

    public EmptyResult clear(EmptyRequest request) throws ResponseException {
        var path = "/db";
        return this.makeRequest("DELETE", path, request, EmptyResult.class, null);
    }

    public void leaveGame(String authToken, int gameId) throws IOException {
        var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE,
                authToken, gameId);
        webSocketCommunicator.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        webSocketCommunicator.session.close();
    }

    public void resignGame(String authToken, int gameId) throws IOException {
        var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN,
                authToken, gameId);
        webSocketCommunicator.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
    }

    public void makeMove(String authToken, int gameId, ChessMove chessMove) throws IOException {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE,
                authToken, gameId, chessMove);
        webSocketCommunicator.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken)
            throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private <T> T makeRequestListGames(String method, String path, ListGamesRequest request, Class<T> responseClass)
            throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setRequestProperty("Authorization", request.authToken());
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            InputStream message = http.getErrorStream();
            InputStreamReader reader = new InputStreamReader(message);
            ErrorMessages response = new Gson().fromJson(reader, ErrorMessages.class);
            throw new ResponseException(status, "failure: " + response.message() + "\n");
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
