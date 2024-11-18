package ui;

import server.ResponseException;
import server.ServerFacade;
import server.requests.LoginRequest;
import server.requests.RegisterRequest;
import server.results.LoginResult;
import server.results.RegisterResult;

import java.util.Arrays;

public class PreLoginClient {

    private final ServerFacade server;
    private final String serverUrl;
    private String userAuthorization;

    public PreLoginClient(String serverUrl, String userAuthorization) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.userAuthorization = userAuthorization;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 1 && params.length < 4) {
            LoginRequest loginRequest = new LoginRequest(params[1], params[2]);
            LoginResult loginResult = server.login(loginRequest);
            userAuthorization = loginResult.authToken();
            return String.format("Welcome %s!", loginRequest.username());
        } else if (params.length < 1 || params.length >= 4) {
            throw new ResponseException(400, "Expected: <username> <password>");
        } else {
            throw new ResponseException(400, "Unexpected parameters");
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1 && params.length < 5) {
            RegisterRequest registerRequest = new RegisterRequest(params[1], params[2], params[3]);
            RegisterResult registerResult = server.register(registerRequest);
            userAuthorization = registerResult.authToken();
            return String.format("Welcome %s!", registerRequest.username());
        } else if (params.length < 1 || params.length >= 5) {
            throw new ResponseException(400, "Expected: <username> <password>");
        } else {
            throw new ResponseException(400, "Unexpected parameters");
        }
    }

    public String help() {
        return """
                - login <username> <password> : to login
                - register <username> <password> <email> : to create an account
                - quit : chess game
                - help : with chess commands
                """;
    }


}
