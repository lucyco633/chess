package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Random;

import static ui.EscapeSequences.*;

public class ChessBoard {

    // Board dimensions
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        ArrayList<ArrayList<String>> chessBoard = createChessBoardArray();

        printChessBoard(out, chessBoard);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void printChessBoard(PrintStream out, ArrayList<ArrayList<String>> chessBoard) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            printRowOfSquares(out, chessBoard.get(boardRow), boardRow);

        }
    }

    private static void printRowOfSquares(PrintStream out, ArrayList<String> chessRow, int rowNum) {
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    if (boardCol == 0 || boardCol == 9) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_WHITE);
                        out.print(EMPTY.repeat(prefixLength));
                        out.print(chessRow.get(boardCol));
                        out.print(EMPTY.repeat(suffixLength));
                    }

                    if (rowNum == 0 || rowNum == 9) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_WHITE);
                        out.print(EMPTY.repeat(prefixLength));
                        out.print(chessRow.get(boardCol));
                        out.print(EMPTY.repeat(suffixLength));
                    }

                    if (rowNum / 2 != 0 && rowNum != 0 && rowNum != 9) {
                        if (boardCol / 2 != 0 && boardCol != 0 && boardCol != 9) {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            checkTeam(out, chessRow.get(boardCol));
                            out.print(EMPTY.repeat(prefixLength));
                            out.print(chessRow.get(boardCol));
                            out.print(EMPTY.repeat(suffixLength));
                        }
                        if (boardCol / 2 == 0 && boardCol != 0 && boardCol != 9) {
                            out.print(SET_BG_COLOR_DARK_GREY);
                            checkTeam(out, chessRow.get(boardCol));
                            out.print(EMPTY.repeat(prefixLength));
                            out.print(chessRow.get(boardCol));
                            out.print(EMPTY.repeat(suffixLength));
                        }
                    }

                    if (rowNum / 2 == 0 && rowNum != 0 && rowNum != 9) {
                        if (boardCol / 2 == 0 && boardCol != 0 && boardCol != 9) {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            checkTeam(out, chessRow.get(boardCol));
                            out.print(EMPTY.repeat(prefixLength));
                            out.print(chessRow.get(boardCol));
                            out.print(EMPTY.repeat(suffixLength));
                        }
                        if (boardCol / 2 == 0 && boardCol != 0 && boardCol != 9) {
                            out.print(SET_BG_COLOR_DARK_GREY);
                            checkTeam(out, chessRow.get(boardCol));
                            out.print(EMPTY.repeat(prefixLength));
                            out.print(chessRow.get(boardCol));
                            out.print(EMPTY.repeat(suffixLength));
                        }
                    }

                } else {
                    if (boardCol == 0 || boardCol == 9) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_WHITE);
                        out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                    }

                    if (rowNum == 0 || rowNum == 9) {
                        out.print(SET_BG_COLOR_BLACK);
                        out.print(SET_TEXT_COLOR_WHITE);
                        out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                    }

                    if (rowNum / 2 != 0 && rowNum != 0 && rowNum != 9) {
                        if (boardCol / 2 != 0 && boardCol != 0 && boardCol != 9) {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        }
                        if (boardCol / 2 == 0 && boardCol != 0 && boardCol != 9) {
                            out.print(SET_BG_COLOR_DARK_GREY);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        }
                    }

                    if (rowNum / 2 == 0 && rowNum != 0 && rowNum != 9) {
                        if (boardCol / 2 == 0 && boardCol != 0 && boardCol != 9) {
                            out.print(SET_BG_COLOR_LIGHT_GREY);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        }
                        if (boardCol / 2 == 0 && boardCol != 0 && boardCol != 9) {
                            out.print(SET_BG_COLOR_DARK_GREY);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        }
                    }
                }

            }

            out.println();
        }
    }

    public static void checkTeam(PrintStream out, String piece) {
        if (piece.equals(BLACK_BISHOP) || piece.equals(BLACK_KNIGHT) || piece.equals(BLACK_ROOK) ||
                piece.equals(BLACK_PAWN) || piece.equals(BLACK_KING) || piece.equals(BLACK_QUEEN)) {
            out.print(SET_TEXT_COLOR_MAGENTA);
        }
        if (piece.equals(WHITE_BISHOP) || piece.equals(WHITE_KNIGHT) || piece.equals(WHITE_ROOK) ||
                piece.equals(WHITE_PAWN) || piece.equals(WHITE_KING) || piece.equals(WHITE_QUEEN)) {
            out.print(SET_TEXT_COLOR_YELLOW);
        } else {
            out.print(SET_TEXT_COLOR_WHITE);
        }
    }

    private static ArrayList<ArrayList<String>> createChessBoardArray() {
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

}
