package ui;

import chess.ChessGame;
import server.ResponseException;
import server.ServerFacade;
import server.ServerMessageHandler;
import server.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Arrays;

public class GameplayClient {
    private int gameID;
    private String authToken;
    private ChessGame chessGame;
    private final WebSocketFacade websocket;
    private String team;


    public GameplayClient(String url, String authToken, int gameID, ChessGame chessGame, String team) throws ResponseException {
        this.authToken = authToken;
        this.gameID = gameID;
        this.chessGame = chessGame;
        this.team = team;
        // how to add notification handler??
        this.websocket = new WebSocketFacade(url, new ServerMessageHandler() {
            @Override
            public void notify(ServerMessage serverMessage) {
            }
        });
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
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String leave(String[] params) throws ResponseException {
        try {
            if (params.length == 0) {

            }
        }
    }

    private String resign(String[] params) {
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
                - resign : forfit a game
                - leave : when you are done
                - help : with chess commands
                """;
    }
}
