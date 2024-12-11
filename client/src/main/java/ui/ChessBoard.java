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


    public static void printReversedChessBoard(PrintStream out, chess.ChessBoard chessBoard, boolean printValid,
                                               ChessGame chessGame, ChessPosition chessPosition) {

        if (chessPosition != null) {
            Collection<ChessMove> validMoves = findValidMoves(chessGame, chessPosition);
            printBoardBlackPerspective(out, chessBoard, validMoves, printValid, chessPosition);
        } else {
            printBoardBlackPerspective(out, chessBoard, null, printValid, chessPosition);
        }
    }

    public static void printChessBoard(PrintStream out, chess.ChessBoard chessBoard, boolean printValid,
                                       ChessGame chessGame, ChessPosition chessPosition) {
        if (chessPosition != null) {
            Collection<ChessMove> validMoves = findValidMoves(chessGame, chessPosition);
            printBoard(out, chessBoard, validMoves, printValid, chessPosition);
        } else {
            printBoard(out, chessBoard, null, printValid, chessPosition);
        }
    }

    public static Collection<ChessMove> findValidMoves(chess.ChessGame chessGame, ChessPosition startPosition) {
        Collection<ChessMove> validMoves = chessGame.validMoves(startPosition);
        return validMoves;
    }

    public static void printSquare(PrintStream out, int row, int col, chess.ChessBoard chessBoard,
                                   Collection<ChessMove> validMoves, boolean highlight, ChessPosition startPosition) {
        ChessPosition chessPosition = new ChessPosition(row, col);
        if (highlight) {
            ChessMove move = new ChessMove(startPosition, chessPosition, null);
            if (validMoves.contains(move)) {
                if (row % 2 == 0 && row != 0 && row != 9) {
                    if (col % 2 == 0 && col != 0 && col != 9) {
                        out.print(SET_BG_COLOR_DARK_GREEN);
                        if (chessBoard.getPiece(chessPosition) != null) {
                            checkTeam(out, chessBoard, row, col);
                        }
                    } else if (col % 2 != 0 && col != 0 && col != 9) {
                        out.print(SET_BG_COLOR_GREEN);
                        if (chessBoard.getPiece(chessPosition) != null) {
                            checkTeam(out, chessBoard, row, col);
                        }
                    }
                } else if (row % 2 != 0 && row != 0 && row != 9) {
                    if (col % 2 == 0 && col != 0 && col != 9) {
                        out.print(SET_BG_COLOR_GREEN);
                        if (chessBoard.getPiece(chessPosition) != null) {
                            checkTeam(out, chessBoard, row, col);
                        }
                    } else if (col % 2 != 0 && col != 0 && col != 9) {
                        out.print(SET_BG_COLOR_DARK_GREEN);
                        if (chessBoard.getPiece(chessPosition) != null) {
                            checkTeam(out, chessBoard, row, col);
                        }
                    }
                }
            } else {
                if (row % 2 == 0 && row != 0 && row != 9) {
                    if (col % 2 == 0 && col != 0 && col != 9) {
                        out.print(SET_BG_COLOR_DARK_GREY);
                        if (chessBoard.getPiece(chessPosition) != null) {
                            checkTeam(out, chessBoard, row, col);
                        }
                    } else if (col % 2 != 0 && col != 0 && col != 9) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        if (chessBoard.getPiece(chessPosition) != null) {
                            checkTeam(out, chessBoard, row, col);
                        }
                    }
                } else if (row % 2 != 0 && row != 0 && row != 9) {
                    if (col % 2 == 0 && col != 0 && col != 9) {
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                        if (chessBoard.getPiece(chessPosition) != null) {
                            checkTeam(out, chessBoard, row, col);
                        }
                    } else if (col % 2 != 0 && col != 0 && col != 9) {
                        out.print(SET_BG_COLOR_DARK_GREY);
                        if (chessBoard.getPiece(chessPosition) != null) {
                            checkTeam(out, chessBoard, row, col);
                        }
                    }
                }
            }
        } else {
            if (row % 2 == 0 && row != 0 && row != 9) {
                if (col % 2 == 0 && col != 0 && col != 9) {
                    out.print(SET_BG_COLOR_DARK_GREY);
                    if (chessBoard.getPiece(chessPosition) != null) {
                        checkTeam(out, chessBoard, row, col);
                    }
                } else if (col % 2 != 0 && col != 0 && col != 9) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    if (chessBoard.getPiece(chessPosition) != null) {
                        checkTeam(out, chessBoard, row, col);
                    }
                }
            } else if (row % 2 != 0 && row != 0 && row != 9) {
                if (col % 2 == 0 && col != 0 && col != 9) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    if (chessBoard.getPiece(chessPosition) != null) {
                        checkTeam(out, chessBoard, row, col);
                    }
                } else if (col % 2 != 0 && col != 0 && col != 9) {
                    out.print(SET_BG_COLOR_DARK_GREY);
                    if (chessBoard.getPiece(chessPosition) != null) {
                        checkTeam(out, chessBoard, row, col);
                    }
                }
            }
        }
        out.print(convertPieceToString(chessBoard, chessPosition));
    }

    public static void printRow(PrintStream out, int row, chess.ChessBoard chessBoard,
                                Collection<ChessMove> validMoves, boolean highlight, ChessPosition chessPosition) {
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            if (col == 0 || col == 9) {
                drawBorder(out, " " + row + " ");
            } else if ((row == 0 || row == 9) && (col != 0 && col != 9)) {
                drawBorder(out, columnToLabel(col));
            } else {
                printSquare(out, row, col, chessBoard, validMoves, highlight, chessPosition);
            }
        }
    }

    public static void printRowBlackPerspective(PrintStream out, int row, chess.ChessBoard chessBoard,
                                                Collection<ChessMove> validMoves, boolean highlight,
                                                ChessPosition chessPosition) {
        for (int col = BOARD_SIZE_IN_SQUARES - 1; col >= 0; col--) {
            if (col == 0 || col == 9) {
                drawBorder(out, " " + row + " ");
            } else if ((row == 0 || row == 9) && (col != 0 && col != 9)) {
                drawBorder(out, columnToLabel(col));
            } else {
                printSquare(out, row, col, chessBoard, validMoves, highlight, chessPosition);
            }
        }
    }

    public static void printBoard(PrintStream out, chess.ChessBoard chessBoard,
                                  Collection<ChessMove> validMoves, boolean highlight, ChessPosition chessPosition) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            printRowBlackPerspective(out, row, chessBoard, validMoves, highlight, chessPosition);
            out.print("\n");
        }
    }

    public static void printBoardBlackPerspective(PrintStream out, chess.ChessBoard chessBoard,
                                                  Collection<ChessMove> validMoves, boolean highlight,
                                                  ChessPosition chessPosition) {
        for (int row = BOARD_SIZE_IN_SQUARES - 1; row >= 0; row--) {
            printRow(out, row, chessBoard, validMoves, highlight, chessPosition);
            out.print("\n");
        }
    }
//
//    private static void printRowOfSquares(PrintStream out, ArrayList<String> chessRow, int rowNum,
//                                          chess.ChessBoard chessBoard, boolean printValid, ChessGame chessGame,
//                                          ChessPosition startPosition, boolean printReversed) {
//        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//            ChessPosition chessPosition = new ChessPosition(rowNum, boardCol);
//            ChessMove chessMove = null;
//            Collection<ChessMove> validMoves = null;
//            if (startPosition != null) {
//                chessMove = new ChessMove(startPosition, chessPosition, null);
//                validMoves = findValidMoves(chessGame, startPosition);
//            }
//            if (boardCol == 0 || boardCol == 9) {
//                drawBorder(out, boardCol, chessRow);
//            }
//
//            if ((rowNum == 0 || rowNum == 9) && (boardCol != 0 && boardCol != 9)) {
//                if (printReversed) {
//                    drawBorder(out, 9 - boardCol, chessRow);
//                } else {
//                    drawBorder(out, boardCol, chessRow);
//                }
//            }
//
//            if (rowNum % 2 != 0 && rowNum != 9) {
//                if (boardCol % 2 != 0 && boardCol != 9) {
//                    if (printReversed) {
//                        printLightSquare(printValid, validMoves, chessMove, out, chessBoard,
//                                rowNum, 9 - boardCol, chessRow, printReversed);
//                    } else {
//                        printLightSquare(printValid, validMoves, chessMove, out, chessBoard,
//                                9 - rowNum, boardCol, chessRow, printReversed);
//                    }
//                }
//                if (boardCol % 2 == 0 && boardCol != 0) {
//                    if (printReversed) {
//                        printDarkSquare(printValid, validMoves, chessMove, out, chessBoard,
//                                rowNum, 9 - boardCol, chessRow, printReversed);
//                    } else {
//                        printDarkSquare(printValid, validMoves, chessMove, out, chessBoard,
//                                9 - rowNum, boardCol, chessRow, printReversed);
//                    }
//                }
//            }
//
//            if (rowNum % 2 == 0 && rowNum != 0 && rowNum != 9) {
//                if (boardCol % 2 == 0 && boardCol != 0) {
//                    if (printReversed) {
//                        printLightSquare(printValid, validMoves, chessMove, out, chessBoard,
//                                rowNum, 9 - boardCol, chessRow, printReversed);
//                    } else {
//                        printLightSquare(printValid, validMoves, chessMove, out, chessBoard,
//                                9 - rowNum, boardCol, chessRow, printReversed);
//                    }
//                }
//                if (boardCol % 2 != 0 && boardCol != 9) {
//                    if (printReversed) {
//                        printDarkSquare(printValid, validMoves, chessMove, out, chessBoard,
//                                rowNum, 9 - boardCol, chessRow, printReversed);
//                    } else {
//                        printDarkSquare(printValid, validMoves, chessMove, out, chessBoard,
//                                9 - rowNum, boardCol, chessRow, printReversed);
//                    }
//                }
//            }
//        }
//
//        out.println();
//    }


//    public static void printDarkSquare(boolean printValid, Collection<ChessMove> validMoves, ChessMove chessMove,
//                                       PrintStream out, chess.ChessBoard chessBoard, int rowNum, int boardCol,
//                                       ArrayList<String> chessRow, boolean printReversed) {
//        ChessPosition chessPosition = new ChessPosition(rowNum, boardCol);
//        if (printValid && validMoves.contains(chessMove)) {
//            out.print(SET_BG_COLOR_DARK_GREEN);
//            if (chessBoard.getPiece(chessPosition) != null) {
//                checkTeam(out, chessBoard, rowNum, boardCol, printReversed);
//            }
//            out.print(chessRow.get(boardCol));
//        } else {
//            out.print(SET_BG_COLOR_DARK_GREY);
//            if (chessBoard.getPiece(chessPosition) != null) {
//                checkTeam(out, chessBoard, rowNum, boardCol, printReversed);
//            }
//            out.print(chessRow.get(boardCol));
//        }
//    }
//
//    public static void printLightSquare(boolean printValid, Collection<ChessMove> validMoves, ChessMove chessMove,
//                                        PrintStream out, chess.ChessBoard chessBoard, int rowNum, int boardCol,
//                                        ArrayList<String> chessRow, boolean printReversed) {
//        ChessPosition chessPosition = new ChessPosition(rowNum, boardCol);
//        ChessPosition newChessPosition = null;
//        if (chessMove != null) {
//            newChessPosition = new ChessPosition(9 - chessMove.getEndPosition().getRow(),
//                    9 - chessMove.getEndPosition().getColumn());
//        }
//        if (printValid && validMoves.contains(chessMove) && newChessPosition != null) {
//            out.print(SET_BG_COLOR_GREEN);
//            if (chessBoard.getPiece(chessPosition) != null) {
//                checkTeam(out, chessBoard, rowNum, boardCol, printReversed);
//            }
//            out.print(chessRow.get(boardCol));
//        } else {
//            out.print(SET_BG_COLOR_LIGHT_GREY);
//            if (chessBoard.getPiece(chessPosition) != null) {
//                checkTeam(out, chessBoard, rowNum, boardCol, printReversed);
//            }
//            out.print(chessRow.get(boardCol));
//        }
//    }

    private static void drawBorder(PrintStream out, String borderLabel) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(borderLabel);
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
//
//
//    public static ArrayList<ArrayList<String>> convertBoardToArray(chess.ChessBoard chessBoard) {
//        ArrayList<ArrayList<String>> chessBoardArray = new ArrayList<>();
//        for (int row = 0; row <= 9; row++) {
//            chessBoardArray.add(row, convertRowToArray(chessBoard, row));
//        }
//        return chessBoardArray;
//    }

    public static String convertRowToString(int rowNum) {
        return " " + rowNum + " ";
    }

    public static String convertPieceToString(chess.ChessBoard chessBoard, ChessPosition chessPosition) {
        if (chessBoard.getPiece(chessPosition) == null) {
            return EMPTY;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KING) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return WHITE_KING;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KING) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return BLACK_KING;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return WHITE_QUEEN;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return BLACK_QUEEN;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return WHITE_KNIGHT;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return BLACK_KNIGHT;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return WHITE_BISHOP;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return BLACK_BISHOP;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return WHITE_ROOK;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return BLACK_ROOK;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return WHITE_PAWN;
        } else if (chessBoard.getPiece(chessPosition).getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                chessBoard.getPiece(chessPosition).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return BLACK_PAWN;
        } else {
            return EMPTY;
        }
    }

    public static String columnToLabel(int col) {
        if (col == 1) {
            return " a ";
        } else if (col == 2) {
            return " b ";
        } else if (col == 3) {
            return " c ";
        } else if (col == 4) {
            return " d ";
        } else if (col == 5) {
            return " e ";
        } else if (col == 6) {
            return " f ";
        } else if (col == 7) {
            return " g ";
        } else if (col == 8) {
            return " h ";
        }
        return EMPTY;
    }

//
//    public static void checkAndSetString(ChessPosition chessPosition, ArrayList<String> chessRowArray,
//                                         chess.ChessBoard chessBoard, int col) {
//        if (chessBoard.getPiece(chessPosition) != null) {
//            convertPieceToString(chessBoard, chessPosition, chessRowArray, col);
//        } else {
//            chessRowArray.add(col, EMPTY);
//        }
//    }

//
//    public static ArrayList<String> convertRowToArray(chess.ChessBoard chessBoard, int row) {
//        ArrayList<String> chessRowArray = new ArrayList<>(10);
//        if (row == 0 | row == 9) {
//            chessRowArray = new ArrayList<>(Arrays.asList
//                    (EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY));
//        } else if (row > 0 && row <= 8) {
//            for (int col = 0; col <= 9; col++) {
//                if (col == 0 | col == 9) {
//                    chessRowArray.add(col, convertRowToString(row));
//                } else {
//                    ChessPosition chessPosition = new ChessPosition(row, col);
//                    checkAndSetString(chessPosition, chessRowArray, chessBoard, col);
//                }
//            }
//        }
//        return chessRowArray;
//    }
}
