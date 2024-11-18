package ui;

import model.GameData;
import server.ResponseException;
import server.ServerFacade;
import server.requests.*;
import server.results.CreateGameResult;
import server.results.JoinGameResult;
import server.results.ListGamesResult;

import java.io.PrintStream;
import java.util.*;

import static java.lang.System.out;

public class PostLoginClient {

    private final ServerFacade server;
    private final String serverUrl;
    private String userAuthorization;
    private Map<Integer, Integer> gameListNewId;
    private int gameNumber = 1;
    private final ChessBoard chessBoard;


    public PostLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.userAuthorization = userAuthorization;
        this.chessBoard = new ChessBoard();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout(String... params) throws ResponseException {
        if (params.length == 1) {
            LogoutRequest logoutRequest = new LogoutRequest(userAuthorization);
            server.logout(logoutRequest);
            return "Goodbye!";
        } else if (params.length != 1) {
            throw new ResponseException(400, "Expected: \"logout\" too many parameters");
        } else {
            throw new ResponseException(400, "Unexpected parameters");
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 2) {
            CreateGameRequest createGameRequest = new CreateGameRequest(params[1], userAuthorization);
            CreateGameResult createGameResult = server.createGame(createGameRequest);
            gameListNewId.put(createGameResult.gameID(), gameNumber);
            return String.format("Created game %s", createGameRequest.gameName());
        } else if (params.length < 2) {
            throw new ResponseException(400, "Expected: <game name> not enough parameters");
        }
        throw new ResponseException(400, "Expected: <game name> too many parameters");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length == 1) {
            ListGamesRequest listGamesRequest = new ListGamesRequest(userAuthorization);
            ListGamesResult listGamesResult = server.listGames(listGamesRequest);
            Collection<String> gamesList = new ArrayList<>();
            //create map to hold onto new game number associated with game
            for (GameData game : listGamesResult.games()) {
                gamesList.add(String.format("%d   Game Name: %s   White Team: %s   Black Team: %s",
                        gameListNewId.get(game.gameID()), game.gameName(),
                        game.whiteUsername(), game.blackUsername()));
            }

            String gamesListString = "";

            for (String game : gamesList) {
                gamesListString = gamesListString + "\n" + game;
            }
            return gamesListString;
        }
        throw new ResponseException(400, "Expected: \"list\" too many parameters");
    }

    public String joinGame(String... params) throws ResponseException {
        //check if ID parameter can be converted to int and is in range of number of games
        try {
            if (params.length == 3) {
                int gameId = 0;
                if (Integer.valueOf(params[1]) > gameNumber) {
                    throw new ResponseException(400, "Invalid Game ID");
                }
                //how to reference game from different ID?
                for (Map.Entry<Integer, Integer> entry : gameListNewId.entrySet()) {
                    if (Objects.equals(Integer.valueOf(params[1]), entry.getValue())) {
                        gameId = entry.getKey();
                    }
                }
                JoinGameRequest joinGameRequest = new JoinGameRequest(params[2], gameId,
                        userAuthorization);
                JoinGameResult joinGameResult = server.joinGame(joinGameRequest);
                //return blank board
                if (params[2] == "WHITE") {
                    chessBoard.printChessBoard(out, chessBoard.createChessBoardArray());
                } else {
                    chessBoard.printReversedChessBoard(out, chessBoard.createChessBoardArray());
                }
                return String.format("Joined game as %s", joinGameResult.playerColor());
            } else if (params.length < 3) {
                throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK] not enough parameters");
            }
            throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK] too many parameters");
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Invalid Game ID");
        }
    }


    public String observeGame(String... params) throws ResponseException {
        //check if ID parameter can be converted to int and is in range of number of games
        try {
            if (params.length == 2) {
                int gameId = 0;
                if (Integer.valueOf(params[1]) > gameNumber) {
                    throw new ResponseException(400, "Invalid Game ID");
                }
                //how to reference game from different ID?
                for (Map.Entry<Integer, Integer> entry : gameListNewId.entrySet()) {
                    if (Objects.equals(Integer.valueOf(params[1]), entry.getValue())) {
                        gameId = entry.getKey();
                    }
                }
                JoinGameRequest joinGameRequest = new JoinGameRequest(params[2], gameId,
                        userAuthorization);
                JoinGameResult joinGameResult = server.joinGame(joinGameRequest);
                //return blank board
                chessBoard.printChessBoard(out, chessBoard.createChessBoardArray());
                return String.format("Observing game %s", params[1]);
            } else if (params.length < 3) {
                throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK] not enough parameters");
            }
            throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK] too many parameters");
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Invalid Game ID");
        }
    }


    public String help() {
        return """
                - create <NAME> : a game
                - list : games
                - join <ID> [WHITE|BLACK] : a game
                - observe <ID> : a game
                - logout : when you are done
                - quit : playing chess
                - help : with chess commands
                """;
    }
}
