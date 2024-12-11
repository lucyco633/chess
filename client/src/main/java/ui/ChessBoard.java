package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.util.*;

import static ui.EscapeSequences.*;

public class ChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 10;


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

    public static void printValidSpot(int row, int col, PrintStream out, chess.ChessBoard chessBoard,
                                      ChessPosition chessPosition) {
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
    }

    public static void printEvenRowSpots(int row, int col, PrintStream out, chess.ChessBoard chessBoard,
                                         ChessPosition chessPosition) {
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
    }

    public static void printOddRowSpots(int row, int col, PrintStream out, chess.ChessBoard chessBoard,
                                        ChessPosition chessPosition) {
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

    public static void printSquare(PrintStream out, int row, int col, chess.ChessBoard chessBoard,
                                   Collection<ChessMove> validMoves, boolean highlight, ChessPosition startPosition) {
        ChessPosition chessPosition = new ChessPosition(row, col);
        if (highlight) {
            ChessMove move = new ChessMove(startPosition, chessPosition, null);
            if (validMoves.contains(move)) {
                printValidSpot(row, col, out, chessBoard, chessPosition);
            } else {
                if (row % 2 == 0 && row != 0 && row != 9) {
                    printEvenRowSpots(row, col, out, chessBoard, chessPosition);
                } else if (row % 2 != 0 && row != 0 && row != 9) {
                    printOddRowSpots(row, col, out, chessBoard, chessPosition);
                }
            }
        } else {
            if (row % 2 == 0 && row != 0 && row != 9) {
                printEvenRowSpots(row, col, out, chessBoard, chessPosition);
            } else if (row % 2 != 0 && row != 0 && row != 9) {
                printOddRowSpots(row, col, out, chessBoard, chessPosition);
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
}