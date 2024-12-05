package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import server.ResponseException;
import server.ServerFacade;
import server.ServerMessageHandler;
import server.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class GameplayClient {
    private int gameID;
    private String authToken;
    private ChessGame chessGame;
    private final ServerFacade server;
    private String team;
    private ChessBoard chessBoard;


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

    private String resign(String... params) {
        try {
            if (params.length == 0) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Are you sure? [y|n] ");
                String line = scanner.nextLine();
                String lineCommandArray[] = line.split(" ", 2);
                String command = lineCommandArray[0];
                if (command.equals("y")) {
                    server.resignGame(authToken, gameID);
                    return "Resigned from game";
                } else if (command.equals("n")) {
                    return "Game not resigned, continue playing";
                } else {
                    return "Expected: [y|n], invalid response";
                }
            } else if (params.length > 0) {
                return "Expected: resign, too many parameters";
            } else {
                return "Unexpected parameters";
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private String makeMove(String... params) {
        try {
            if (params.length == 4) {
                int startRow = parsePosition(params[0]);
                int startColumn = parsePosition(params[1]);
                int endRow = parsePosition(params[2]);
                int endColumn = parsePosition(params[3]);
                if (startRow == 0 | endRow == 0 | startColumn == 0 | endColumn == 0) {
                    return "Expected: move <START ROW> <START COLUMN> <END ROW> <END COLUMN>, invalid input";
                }
                ChessPosition start = new ChessPosition(startRow, startColumn);
                ChessPosition end = new ChessPosition(endRow, endColumn);
                //how to handle promotion piece?
                ChessPiece.PieceType promotionPiece = null;
                if (endRow == 8 && Objects.equals(team, "WHITE") &&
                        chessGame.getBoard().getPiece(start).getPieceType().equals(ChessPiece.PieceType.PAWN)) {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("How would you like to promote?");
                    String line = scanner.nextLine();
                    String lineCommandArray[] = line.split(" ", 2);
                    String command = lineCommandArray[0];
                    if (command.equals("Q")) {
                        promotionPiece = ChessPiece.PieceType.QUEEN;
                    } else if (command.equals("N")) {
                        promotionPiece = ChessPiece.PieceType.KNIGHT;
                    } else if (command.equals("R")) {
                        promotionPiece = ChessPiece.PieceType.ROOK;
                    } else if (command.equals("B")) {
                        promotionPiece = ChessPiece.PieceType.BISHOP;
                    } else {
                        return "Invalid response";
                    }
                }
                server.makeMove(authToken, gameID, new ChessMove(start, end, promotionPiece));
                return "Move made to row " + endRow + " column " + endColumn;
            } else if (params.length < 4) {
                return "Expected: move <START ROW> <START COLUMN> <END ROW> <END COLUMN>, not enough parameters";
            }
            return "Expected: move <START ROW> <START COLUMN> <END ROW> <END COLUMN>, too many parameters";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private String highlightMoves(String... params) {
        if (params.length == 2) {
            int row = parsePosition(params[0]);
            int col = parsePosition(params[1]);
            if (team == "WHITE" && row != 0 && col != 0) {
                chessBoard.printChessBoard(System.out, chessGame.getBoard(), true,
                        chessGame, new ChessPosition(row, col));
                return "Chess Board";
            } else if (team == "BLACK" && row != 0 && col != 0) {
                chessBoard.printReversedChessBoard(System.out, chessGame.getBoard(), true,
                        chessGame, new ChessPosition(row, col));
                return "Chess Board";
            } else if (row == 0 | col == 0) {
                return "Expected: highlight <ROW> <COLUMN>, invalid row or column";
            }
        }
        return "Expected: highlight <ROW> <COLUMN>";
    }

    private String redrawBoard(String... params) {
        if (params.length == 0) {
            if (team == "WHITE") {
                chessBoard.printChessBoard(System.out, chessGame.getBoard(), false,
                        chessGame, null);
                return "Chess Board";
            } else if (team == "BLACK") {
                chessBoard.printReversedChessBoard(System.out, chessGame.getBoard(), false,
                        chessGame, null);
                return "Chess Board";
            }
        }
        return "Expected: redraw, too many parameters";
    }

    private int parsePosition(String pos) {
        int position = 0;
        if (pos.equals("a") | pos.equals("1")) {
            position = 1;
        } else if (pos.equals("b") | pos.equals("2")) {
            position = 2;
        } else if (pos.equals("c") | pos.equals("3")) {
            position = 3;
        } else if (pos.equals("d") | pos.equals("4")) {
            position = 4;
        } else if (pos.equals("e") | pos.equals("5")) {
            position = 5;
        } else if (pos.equals("f") | pos.equals("6")) {
            position = 6;
        } else if (pos.equals("g") | pos.equals("7")) {
            position = 7;
        } else if (pos.equals("h") | pos.equals("8")) {
            position = 8;
        }
        return position;
    }

    public String help() {
        return """
                - redraw : your chess board
                - highlight : possible moves
                - move <START ROW> <START COLUMN> <END ROW> <END COLUMN> : make a move
                - resign : forfeit a game
                - leave : when you are done
                - help : with chess commands
                """;
    }
}
