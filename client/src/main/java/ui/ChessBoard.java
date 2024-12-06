package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;

public class ChessBoard {

    // Board dimensions
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;


    public void printChessBoard(PrintStream out, chess.ChessBoard chessBoard, boolean printValid,
                                ChessGame chessGame, ChessPosition chessPosition) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            ArrayList<ArrayList<String>> chessBoardArray = convertBoardToArray(chessBoard);

            printRowOfSquares(out, chessBoardArray.get(boardRow), boardRow, chessBoard, printValid,
                    chessGame, chessPosition);

        }
    }

    public void printReversedChessBoard(PrintStream out, chess.ChessBoard chessBoard, boolean printValid,
                                        ChessGame chessGame, ChessPosition chessPosition) {

        for (int boardRow = BOARD_SIZE_IN_SQUARES - 1; boardRow >= 0; --boardRow) {

            ArrayList<ArrayList<String>> chessBoardArray = convertBoardToArray(chessBoard);

            Collections.reverse(chessBoardArray.get(boardRow));

            printRowOfSquares(out, chessBoardArray.get(boardRow), 9 - boardRow, chessBoard, printValid,
                    chessGame, chessPosition);

        }
    }


    public static Collection<ChessMove> findValidMoves(chess.ChessGame chessGame, ChessPosition startPosition) {
        Collection<ChessMove> validMoves = chessGame.validMoves(startPosition);
        return validMoves;
    }

    private static void printRowOfSquares(PrintStream out, ArrayList<String> chessRow, int rowNum,
                                          chess.ChessBoard chessBoard, boolean printValid, ChessGame chessGame,
                                          ChessPosition startPosition) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            ChessPosition chessPosition = new ChessPosition(rowNum, boardCol);
            ChessMove chessMove = null;
            Collection<ChessMove> validMoves = null;
            if (startPosition != null) {
                chessMove = new ChessMove(startPosition, chessPosition, null);
                validMoves = findValidMoves(chessGame, startPosition);
            }
            if (boardCol == 0 || boardCol == 9) {
                drawBorder(out, boardCol, chessRow);
            }

            if ((rowNum == 0 || rowNum == 9) && (boardCol != 0 && boardCol != 9)) {
                drawBorder(out, boardCol, chessRow);
            }

            if (rowNum % 2 != 0 && rowNum != 9) {
                if (boardCol % 2 != 0 && boardCol != 9) {
                    printLightSquare(printValid, validMoves, chessMove, out, chessBoard, rowNum, boardCol, chessRow);
                }
                if (boardCol % 2 == 0 && boardCol != 0) {
                    printDarkSquare(printValid, validMoves, chessMove, out, chessBoard, rowNum, boardCol, chessRow);
                }
            }

            if (rowNum % 2 == 0 && rowNum != 0 && rowNum != 9) {
                if (boardCol % 2 == 0 && boardCol != 0) {
                    printLightSquare(printValid, validMoves, chessMove, out, chessBoard, rowNum, boardCol, chessRow);
                }
                if (boardCol % 2 != 0 && boardCol != 9) {
                    printDarkSquare(printValid, validMoves, chessMove, out, chessBoard, rowNum, boardCol, chessRow);
                }
            }
        }

        out.println();
    }

    public static void printDarkSquare(boolean printValid, Collection<ChessMove> validMoves, ChessMove chessMove,
                                       PrintStream out, chess.ChessBoard chessBoard, int rowNum, int boardCol,
                                       ArrayList<String> chessRow) {
        ChessPosition chessPosition = new ChessPosition(rowNum, boardCol);
        if (printValid && validMoves.contains(chessMove)) {
            out.print(SET_BG_COLOR_DARK_GREEN);
            if (chessBoard.getPiece(chessPosition) != null) {
                checkTeam(out, chessBoard, rowNum, boardCol);
            }
            out.print(chessRow.get(boardCol));
        } else {
            out.print(SET_BG_COLOR_DARK_GREY);
            if (chessBoard.getPiece(chessPosition) != null) {
                checkTeam(out, chessBoard, rowNum, boardCol);
            }
            out.print(chessRow.get(boardCol));
        }
    }

    public static void printLightSquare(boolean printValid, Collection<ChessMove> validMoves, ChessMove chessMove,
                                        PrintStream out, chess.ChessBoard chessBoard, int rowNum, int boardCol,
                                        ArrayList<String> chessRow) {
        ChessPosition chessPosition = new ChessPosition(rowNum, boardCol);
        if (printValid && validMoves.contains(chessMove)) {
            out.print(SET_BG_COLOR_GREEN);
            if (chessBoard.getPiece(chessPosition) != null) {
                checkTeam(out, chessBoard, rowNum, boardCol);
            }
            out.print(chessRow.get(boardCol));
        } else {
            out.print(SET_BG_COLOR_LIGHT_GREY);
            if (chessBoard.getPiece(chessPosition) != null) {
                checkTeam(out, chessBoard, rowNum, boardCol);
            }
            out.print(chessRow.get(boardCol));
        }
    }

    private static void drawBorder(PrintStream out, int boardCol, ArrayList<String> chessRow) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(chessRow.get(boardCol));
    }

    public static void checkTeam(PrintStream out, chess.ChessBoard chessBoard, int row, int col) {
        ChessPosition chessPosition = new ChessPosition(row, col);
        if (chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            out.print(SET_TEXT_COLOR_BLUE);

        } else if (chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            out.print(SET_TEXT_COLOR_YELLOW);
        }
    }

    public ArrayList<ArrayList<String>> createChessBoardArray() {
        ArrayList<ArrayList<String>> chessBoard = new ArrayList<>();

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY)));

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (" 8 ", BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
                                BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, " 8 ")));

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (" 7 ", BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN,
                                BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, " 7 ")));

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (" 6 ", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, " 6 ")));

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (" 5 ", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, " 5 ")));

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (" 4 ", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, " 4 ")));

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (" 3 ", EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, " 3 ")));

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (" 2 ", WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN,
                                WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, " 2 ")));

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (" 1 ", WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
                                WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, " 1 ")));

        chessBoard.add(
                new ArrayList<>(Arrays.asList
                        (EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY)));

        return chessBoard;
    }


    public ArrayList<ArrayList<String>> convertBoardToArray(chess.ChessBoard chessBoard) {
        ArrayList<ArrayList<String>> chessBoardArray = new ArrayList<>();
        for (int row = 0; row <= 9; row++) {
            chessBoardArray.add(row, convertRowToArray(chessBoard, row));
        }
        return chessBoardArray;
    }

    public String convertRowToString(int rowNum) {
        return " " + rowNum + " ";
    }

    public void convertPieceToString(chess.ChessBoard chessBoard, ChessPosition chessPosition,
                                     ArrayList<String> chessRowArray, int col) {
        if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KING) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            chessRowArray.add(col, WHITE_KING);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KING) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            chessRowArray.add(col, BLACK_KING);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            chessRowArray.add(col, WHITE_QUEEN);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            chessRowArray.add(col, BLACK_QUEEN);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            chessRowArray.add(col, WHITE_KNIGHT);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            chessRowArray.add(col, BLACK_KNIGHT);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            chessRowArray.add(col, WHITE_BISHOP);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            chessRowArray.add(col, BLACK_BISHOP);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            chessRowArray.add(col, WHITE_ROOK);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            chessRowArray.add(col, BLACK_ROOK);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            chessRowArray.add(col, WHITE_PAWN);
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            chessRowArray.add(col, BLACK_PAWN);
        } else {
            chessRowArray.add(col, EMPTY);
        }
    }


    public void checkAndSetString(ChessPosition chessPosition, ArrayList<String> chessRowArray,
                                  chess.ChessBoard chessBoard, int col) {
        if (chessBoard.getPiece(chessPosition) != null) {
            convertPieceToString(chessBoard, chessPosition, chessRowArray, col);
        } else {
            chessRowArray.add(col, EMPTY);
        }
    }


    public ArrayList<String> convertRowToArray(chess.ChessBoard chessBoard, int row) {
        ArrayList<String> chessRowArray = new ArrayList<>(10);
        if (row == 0 | row == 9) {
            chessRowArray = new ArrayList<>(Arrays.asList
                    (EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY));
        } else if (row > 0 && row <= 8) {
            for (int col = 0; col <= 9; col++) {
                if (col == 0 | col == 9) {
                    chessRowArray.add(col, convertRowToString(row));
                } else {
                    ChessPosition chessPosition = new ChessPosition(row, col);
                    checkAndSetString(chessPosition, chessRowArray, chessBoard, col);
                }
            }
        }
        return chessRowArray;
    }
}
