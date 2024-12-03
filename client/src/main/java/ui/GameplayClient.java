package ui;

import chess.ChessGame;
import server.ResponseException;
import server.ServerFacade;
import server.ServerMessageHandler;
import server.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class GameplayClient {
    private int gameID;
    private String authToken;
    private ChessGame chessGame;
    private final ServerFacade server;
    private String team;


    public GameplayClient(String url, String authToken, int gameID, ChessGame chessGame, String team) throws ResponseException {
        this.authToken = authToken;
        this.gameID = gameID;
        this.chessGame = chessGame;
        this.team = team;
        server = new ServerFacade(url);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redrawBoard(params);
                case "highlight" -> highlightMoves(params);
                case "move" -> makeMove(params);
                case "resign" -> resign(params);
                case "leave" -> leave(params);
                case "y" -> "y";
                case "n" -> "n";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String leave(String[] params) throws ResponseException {
        try {
            if (params.length == 0) {
                server.leaveGame(authToken, gameID);
                return "notify?";
            } else if (params.length > 0) {
                return "Too many parameters";
            } else {
                return "Unexpected parameters";
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private String resign(String[] params) {
        try {
            if (params.length == 0) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Are you sure?");
                String line = scanner.nextLine();
                String lineCommandArray[] = line.split(" ", 2);
                String command = lineCommandArray[0];
                if (command.equals("y")) {
                    server.resignGame(authToken, gameID);
                    return "Resigned from game";
                } else if (command.equals("n")) {
                    return "Game not resigned, continue playing";
                } else {
                    return "Invalid response";
                }
            } else if (params.length > 0) {
                return "Too many parameters";
            } else {
                return "Unexpected parameters";
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private String makeMove(String[] params) {
    }

    private String highlightMoves(String[] params) {
    }

    private String redrawBoard(String[] params) {
    }

    public String help() {
        return """
                - redraw : your chess board
                - highlight : possible moves
                - move <START> <END> : make a move
                - resign : forfeit a game
                - leave : when you are done
                - help : with chess commands
                """;
    }
}
