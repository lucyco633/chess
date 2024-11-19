package ui;

import org.junit.jupiter.api.Test;
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

    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
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
        try {
            if (params.length == 2) {
                LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
                LoginResult loginResult = server.login(loginRequest);
                userAuthorization = loginResult.authToken();
                sendToPostLogin();
                return String.format("Welcome %s!", loginRequest.username());
            } else if (params.length >= 2) {
                throw new ResponseException(400, "Expected: <username> <password> too many parameters\n");
            } else if (params.length < 2) {
                throw new ResponseException(400, "Expected: <username> <password> not enough parameters\n");
            } else {
                throw new ResponseException(400, "Unexpected parameters\n");
            }
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        try {
            if (params.length >= 1 && params.length < 4) {
                RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
                RegisterResult registerResult = server.register(registerRequest);
                userAuthorization = registerResult.authToken();
                sendToPostLogin();
                return String.format("Welcome %s!", registerRequest.username());
            } else if (params.length < 1 || params.length >= 5) {
                return "Expected: <username> <password>\n";
            } else {
                return "Unexpected parameters\n";
            }
        } catch (ResponseException e) {
            return e.getMessage();
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

    //add getauth function


    public String getServerUrl() {
        return serverUrl;
    }

    public String getUserAuthorization() {
        return userAuthorization;
    }

    private void sendToPostLogin() {
        PostLoginRepl postLoginRepl = new PostLoginRepl(getServerUrl(), getUserAuthorization());
        postLoginRepl.run();
    }
}
