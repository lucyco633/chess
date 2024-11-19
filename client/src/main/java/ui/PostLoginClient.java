package ui;

import model.GameData;
import server.ResponseException;
import server.ServerFacade;
import server.requests.*;
import server.results.CreateGameResult;
import server.results.JoinGameResult;
import server.results.ListGamesResult;

import java.util.*;

import static java.lang.System.out;

public class PostLoginClient {

    private final ServerFacade server;
    private String userAuthorization;
    private static Map<Integer, Integer> gameListNewId;
    private static int gameNumber;
    private final ChessBoard chessBoard;


    public PostLoginClient(String serverUrl, String userAuthorization) {
        server = new ServerFacade(serverUrl);
        this.userAuthorization = userAuthorization;
        this.chessBoard = new ChessBoard();
        this.gameListNewId = new HashMap<>();
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
        try {
            if (params.length == 0) {
                LogoutRequest logoutRequest = new LogoutRequest(userAuthorization);
                server.logout(logoutRequest);
                return "Goodbye!";
            } else if (params.length != 0) {
                return "Expected: logout, too many parameters\n";
            } else {
                return "Unexpected parameters\n";
            }
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        try {
            if (params.length == 1) {
                CreateGameRequest createGameRequest = new CreateGameRequest(params[0], userAuthorization);
                CreateGameResult createGameResult = server.createGame(createGameRequest);
                gameNumber++;
                gameListNewId.put(createGameResult.gameID(), gameNumber);
                return String.format("Created game %s", createGameRequest.gameName());
            } else if (params.length < 1) {
                return "Expected: <game name> not enough parameters\n";
            } else if (params.length > 1) {
                return "Expected: <game name> too many parameters\n";
            }
            return "Unexpected parameters\n";
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String listGames(String... params) throws ResponseException {
        try {
            if (params.length == 0) {
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
            return "Expected: list, too many parameters\n";
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String joinGame(String... params) throws ResponseException {
        //check if ID parameter can be converted to int and is in range of number of games
        try {
            if (params.length == 2) {
                int gameId = 0;
                if (Integer.valueOf(params[0]) > gameNumber) {
                    return "Invalid Game ID\n";
                }
                //how to reference game from different ID?
                for (Map.Entry<Integer, Integer> entry : gameListNewId.entrySet()) {
                    if (Objects.equals(Integer.valueOf(params[0]), entry.getValue())) {
                        gameId = entry.getKey();
                    }
                }
                JoinGameRequest joinGameRequest = new JoinGameRequest(params[1], gameId,
                        userAuthorization);
                JoinGameResult joinGameResult = server.joinGame(joinGameRequest);
                //return blank board
                if (params[1].equals("white")) {
                    chessBoard.printChessBoard(out, chessBoard.createChessBoardArray());
                } else if (params[1].equals("black")) {
                    chessBoard.printReversedChessBoard(out, chessBoard.createChessBoardArray());
                }
                return String.format("Joined game as %s", joinGameResult.playerColor());
            } else if (params.length < 2) {
                return "Expected: <ID> [WHITE|BLACK] not enough parameters\n";
            }
            return "Expected: <ID> [WHITE|BLACK] too many parameters\n";
        } catch (NumberFormatException e) {
            return "Invalid Game ID\n";
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }


    public String observeGame(String... params) throws ResponseException {
        //check if ID parameter can be converted to int and is in range of number of games
        try {
            if (params.length == 1) {
                int gameId = 0;
                if (Integer.valueOf(params[0]) > gameNumber) {
                    throw new ResponseException(400, "Invalid Game ID\n");
                }
                //how to reference game from different ID?
                for (Map.Entry<Integer, Integer> entry : gameListNewId.entrySet()) {
                    if (Objects.equals(Integer.valueOf(params[0]), entry.getValue())) {
                        gameId = entry.getKey();
                    }
                }
                chessBoard.printChessBoard(out, chessBoard.createChessBoardArray());
                return String.format("Observing game %s", params[0]);
            } else if (params.length < 3) {
                return "Expected: <ID> [WHITE|BLACK] not enough parameters\n";
            }
            return "Expected: <ID> [WHITE|BLACK] too many parameters\n";
        } catch (NumberFormatException e) {
            return "Invalid Game ID\n";
        } catch (ResponseException e) {
            return e.getMessage();
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
