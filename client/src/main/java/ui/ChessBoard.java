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


    public void printChessBoard(PrintStream out, chess.ChessBoard chessBoard) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            ArrayList<ArrayList<String>> chessBoardArray = convertBoardToArray(chessBoard);

            printRowOfSquares(out, chessBoardArray.get(boardRow), boardRow, chessBoard);

        }
    }

    public void printReversedChessBoard(PrintStream out, chess.ChessBoard chessBoard) {

        for (int boardRow = BOARD_SIZE_IN_SQUARES - 1; boardRow >= 0; --boardRow) {

            ArrayList<ArrayList<String>> chessBoardArray = convertBoardToArray(chessBoard);

            Collections.reverse(chessBoardArray.get(boardRow));

            printRowOfSquares(out, chessBoardArray.get(boardRow), 9 - boardRow, chessBoard);

        }
    }

    private Collection<ChessMove> findValidMoves(chess.ChessGame chessGame, ChessPosition startPosition) {
        Collection<ChessMove> validMoves = chessGame.validMoves(startPosition);
        return validMoves;
    }

    private static void printRowOfSquares(PrintStream out, ArrayList<String> chessRow, int rowNum,
                                          chess.ChessBoard chessBoard) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (boardCol == 0 || boardCol == 9) {
                drawBorder(out, boardCol, chessRow);
            }

            if ((rowNum == 0 || rowNum == 9) && (boardCol != 0 && boardCol != 9)) {
                drawBorder(out, boardCol, chessRow);
            }

            if (rowNum % 2 != 0 && rowNum != 9) {
                if (boardCol % 2 != 0 && boardCol != 9) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    checkTeam(out, chessBoard, rowNum, boardCol);
                    out.print(chessRow.get(boardCol));
                }
                if (boardCol % 2 == 0 && boardCol != 0) {
                    out.print(SET_BG_COLOR_DARK_GREY);
                    checkTeam(out, chessBoard, rowNum, boardCol);
                    out.print(chessRow.get(boardCol));
                }
            }

            if (rowNum % 2 == 0 && rowNum != 0 && rowNum != 9) {
                if (boardCol % 2 == 0 && boardCol != 0) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    checkTeam(out, chessBoard, rowNum, boardCol);
                    out.print(chessRow.get(boardCol));
                }
                if (boardCol % 2 != 0 && boardCol != 9) {
                    out.print(SET_BG_COLOR_DARK_GREY);
                    checkTeam(out, chessBoard, rowNum, boardCol);
                    out.print(chessRow.get(boardCol));
                }
            }
        }

        out.println();
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
        for (int row = 0; row <= 8; row++) {
            chessBoardArray.add(row, convertRowToArray(chessBoard, row));
        }
        return chessBoardArray;
    }


    public ArrayList<String> convertRowToArray(chess.ChessBoard chessBoard, int row) {
        ArrayList<String> chessRowArray = new ArrayList<>();
        if (row == 0 | row == 9) {
            chessRowArray = new ArrayList<>(Arrays.asList
                    (EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY));
        } else if (row == 1) {
            chessRowArray.set(0, " 1 ");
            chessRowArray.set(9, " 1 ");
        } else if (row == 2) {
            chessRowArray.set(0, " 2 ");
            chessRowArray.set(9, " 2 ");
        } else if (row == 3) {
            chessRowArray.set(0, " 3 ");
            chessRowArray.set(9, " 3 ");
        } else if (row == 4) {
            chessRowArray.set(0, " 4 ");
            chessRowArray.set(9, " 4 ");
        } else if (row == 5) {
            chessRowArray.set(0, " 5 ");
            chessRowArray.set(9, " 5 ");
        } else if (row == 6) {
            chessRowArray.set(0, " 6 ");
            chessRowArray.set(9, " 6 ");
        } else if (row == 7) {
            chessRowArray.set(0, " 7 ");
            chessRowArray.set(9, " 7 ");
        } else if (row == 8) {
            chessRowArray.set(0, " 8 ");
            chessRowArray.set(9, " 8 ");
        }
        for (int col = 1; col <= 8; col++) {
            ChessPosition chessPosition = new ChessPosition(row, col);
            if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KING) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                chessRowArray.set(col, WHITE_KING);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KING) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                chessRowArray.set(col, BLACK_KING);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                chessRowArray.set(col, WHITE_QUEEN);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                chessRowArray.set(col, BLACK_QUEEN);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                chessRowArray.set(col, WHITE_KNIGHT);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                chessRowArray.set(col, BLACK_KNIGHT);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                chessRowArray.set(col, WHITE_BISHOP);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                chessRowArray.set(col, BLACK_BISHOP);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                chessRowArray.set(col, WHITE_ROOK);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                chessRowArray.set(col, BLACK_ROOK);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                chessRowArray.set(col, WHITE_PAWN);
            } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                    chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                chessRowArray.set(col, BLACK_PAWN);
            } else {
                chessRowArray.set(col, EMPTY);
            }
        }
        return chessRowArray;
    }
}
