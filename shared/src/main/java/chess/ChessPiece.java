package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public ChessMove checkMove(ChessBoard board, ChessPosition myPosition, int vertical,
                               int horizontal, PieceType promotion) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + vertical,
                myPosition.getColumn() + horizontal);
        if (board.getPiece(newPosition) == null) {
            return new ChessMove(myPosition, newPosition, promotion);
        } else if ((board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.BLACK &&
                board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                | (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.WHITE &&
                board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)) {
            if (board.getPiece(myPosition).type == PieceType.PAWN && horizontal == 0) {
                return null;
            }
            return new ChessMove(myPosition, newPosition, promotion);
        } else if ((board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.BLACK &&
                board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)
                | (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.WHITE &&
                board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)) {
            return null;
        }
        return null;
    }


    public void validPieceMove(ChessBoard board, ChessPosition myPosition, int vertical, int horizontal,
                               PieceType promotion, Collection<ChessMove> valid) {
        if ((vertical == 1 && horizontal == 1) | (vertical == 1 && horizontal == 0) |
                (vertical == 0 && horizontal == 1)) {
            while (((myPosition.getRow() + vertical) <= 8) && ((myPosition.getColumn() + horizontal) <= 8)) {
                ChessMove okayMove = checkMove(board, myPosition, vertical, horizontal, promotion);
                if (vertical != 0) {
                    vertical++;
                }
                if (horizontal != 0) {
                    horizontal++;
                }
                if (okayMove != null) {
                    valid.add(okayMove);
                    if (board.getPiece(okayMove.getEndPosition()) != null) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if ((vertical == -1 && horizontal == -1) | (vertical == -1 && horizontal == 0) |
                (vertical == 0 && horizontal == -1)) {
            while (((myPosition.getRow() + vertical) > 0) && ((myPosition.getColumn() + horizontal) > 0)) {
                ChessMove okayMove = checkMove(board, myPosition, vertical, horizontal, promotion);
                if (vertical != 0) {
                    vertical--;
                }
                if (horizontal != 0) {
                    horizontal--;
                }
                if (okayMove != null) {
                    valid.add(okayMove);
                    if (board.getPiece(okayMove.getEndPosition()) != null) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (vertical == 1 && horizontal == -1) {
            while (((myPosition.getRow() + vertical) <= 8) && ((myPosition.getColumn() + horizontal) > 0)) {
                ChessMove okayMove = checkMove(board, myPosition, vertical, horizontal, promotion);
                vertical++;
                horizontal--;
                if (okayMove != null) {
                    valid.add(okayMove);
                    if (board.getPiece(okayMove.getEndPosition()) != null) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (vertical == -1 && horizontal == 1) {
            while (((myPosition.getRow() + vertical) > 0) && ((myPosition.getColumn() + horizontal) <= 8)) {
                ChessMove okayMove = checkMove(board, myPosition, vertical, horizontal, promotion);
                vertical--;
                horizontal++;
                if (okayMove != null) {
                    valid.add(okayMove);
                    if (board.getPiece(okayMove.getEndPosition()) != null) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
    }

    public void validKnightMove(ChessBoard board, ChessPosition myPosition, int vertical, int horizontal,
                                PieceType promotion, Collection<ChessMove> valid) {
        if ((vertical == 2 && horizontal == 1) | (vertical == 2 && horizontal == -1) |
                (vertical == -2 && horizontal == 1) | (vertical == -2 && horizontal == -1) |
                (vertical == 1 && horizontal == 2) | (vertical == -1 && horizontal == 2) |
                (vertical == -1 && horizontal == -2) | (vertical == 1 && horizontal == -2)) {
            if (((myPosition.getRow() + vertical) > 0) && ((myPosition.getColumn() + horizontal) <= 8) &&
                    ((myPosition.getRow() + vertical) <= 8) && ((myPosition.getColumn() + horizontal) > 0)) {
                ChessMove okayMove = checkMove(board, myPosition, vertical, horizontal, promotion);
                if (okayMove != null) {
                    valid.add(okayMove);
                }
            }
        }
    }


    public void validPawnMoveBlack(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> valid) {
        if (myPosition.getRow() == 7) {
            ChessMove okayMoveDownTwo = checkMove(board, myPosition, -2, 0, null);
            ChessMove okayMoveDownOne = checkMove(board, myPosition, -1, 0, null);
            if (okayMoveDownTwo != null && okayMoveDownOne != null) {
                valid.add(okayMoveDownTwo);
            }
            if (okayMoveDownOne != null) {
                valid.add(okayMoveDownOne);
            }
        }
        if (((myPosition.getColumn() - 1) > 0) && (myPosition.getRow() - 1) > 0) {
            ChessPosition attackLeft = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
            if (board.getPiece(attackLeft) != null && board.getPiece(attackLeft).pieceColor == ChessGame.TeamColor.WHITE) {
                if (attackLeft.getRow() == 1) {
                    ChessMove okayPromoteQueen = checkMove(board, myPosition, -1, -1, PieceType.QUEEN);
                    ChessMove okayPromoteBishop = checkMove(board, myPosition, -1, -1, PieceType.BISHOP);
                    ChessMove okayPromoteKnight = checkMove(board, myPosition, -1, -1, PieceType.KNIGHT);
                    ChessMove okayPromoteRook = checkMove(board, myPosition, -1, -1, PieceType.ROOK);
                    if (okayPromoteQueen != null) {
                        valid.add(okayPromoteQueen);
                    }
                    if (okayPromoteBishop != null) {
                        valid.add(okayPromoteBishop);
                    }
                    if (okayPromoteKnight != null) {
                        valid.add(okayPromoteKnight);
                    }
                    if (okayPromoteRook != null) {
                        valid.add(okayPromoteRook);
                    }
                } else {
                    ChessMove okayAttack = checkMove(board, myPosition, -1, -1, null);
                    if (okayAttack != null) {
                        valid.add(okayAttack);
                    }
                }
            }
        }
        if (((myPosition.getColumn() + 1) <= 8) && (myPosition.getRow() - 1) > 0) {
            ChessPosition attackRight = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
            if (board.getPiece(attackRight) != null && board.getPiece(attackRight).pieceColor == ChessGame.TeamColor.WHITE) {
                if (attackRight.getRow() == 1) {
                    ChessMove okayPromoteQueen = checkMove(board, myPosition, -1, 1, PieceType.QUEEN);
                    ChessMove okayPromoteBishop = checkMove(board, myPosition, -1, 1, PieceType.BISHOP);
                    ChessMove okayPromoteKnight = checkMove(board, myPosition, -1, 1, PieceType.KNIGHT);
                    ChessMove okayPromoteRook = checkMove(board, myPosition, -1, 1, PieceType.ROOK);
                    if (okayPromoteQueen != null) {
                        valid.add(okayPromoteQueen);
                    }
                    if (okayPromoteBishop != null) {
                        valid.add(okayPromoteBishop);
                    }
                    if (okayPromoteKnight != null) {
                        valid.add(okayPromoteKnight);
                    }
                    if (okayPromoteRook != null) {
                        valid.add(okayPromoteRook);
                    }
                } else {
                    ChessMove okayAttack = checkMove(board, myPosition, -1, 1, null);
                    if (okayAttack != null) {
                        valid.add(okayAttack);
                    }
                }
            }
        }
        if (((myPosition.getRow() - 1) == 1)) {
            ChessMove okayPromoteQueen = checkMove(board, myPosition, -1, 0, PieceType.QUEEN);
            ChessMove okayPromoteBishop = checkMove(board, myPosition, -1, 0, PieceType.BISHOP);
            ChessMove okayPromoteKnight = checkMove(board, myPosition, -1, 0, PieceType.KNIGHT);
            ChessMove okayPromoteRook = checkMove(board, myPosition, -1, 0, PieceType.ROOK);
            if (okayPromoteQueen != null) {
                valid.add(okayPromoteQueen);
            }
            if (okayPromoteBishop != null) {
                valid.add(okayPromoteBishop);
            }
            if (okayPromoteKnight != null) {
                valid.add(okayPromoteKnight);
            }
            if (okayPromoteRook != null) {
                valid.add(okayPromoteRook);
            }
        }
        if ((myPosition.getColumn() > 0 && myPosition.getColumn() <= 8) && ((myPosition.getRow() - 1) > 1)) {
            ChessMove okayMove = checkMove(board, myPosition, -1, 0, null);
            if (!valid.contains(okayMove) && okayMove != null) {
                valid.add(okayMove);
            }
        }

    }


    public void validPawnMoveWhite(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> valid) {
        if (myPosition.getRow() == 2) {
            ChessMove okayMoveUpTwo = checkMove(board, myPosition, 2, 0, null);
            ChessMove okayMoveUpOne = checkMove(board, myPosition, 1, 0, null);
            if (okayMoveUpOne != null) {
                valid.add(okayMoveUpOne);
            }
            if (okayMoveUpTwo != null && okayMoveUpOne != null) {
                valid.add(okayMoveUpTwo);
            }
        }
        if (((myPosition.getColumn() - 1) > 0) && (myPosition.getRow() + 1) <= 8) {
            ChessPosition attackLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
            if (board.getPiece(attackLeft) != null && board.getPiece(attackLeft).pieceColor == ChessGame.TeamColor.BLACK) {
                if (attackLeft.getRow() == 8) {
                    ChessMove okayPromoteQueen = checkMove(board, myPosition, 1, -1, PieceType.QUEEN);
                    ChessMove okayPromoteBishop = checkMove(board, myPosition, 1, -1, PieceType.BISHOP);
                    ChessMove okayPromoteKnight = checkMove(board, myPosition, 1, -1, PieceType.KNIGHT);
                    ChessMove okayPromoteRook = checkMove(board, myPosition, 1, -1, PieceType.ROOK);
                    if (okayPromoteQueen != null) {
                        valid.add(okayPromoteQueen);
                    }
                    if (okayPromoteBishop != null) {
                        valid.add(okayPromoteBishop);
                    }
                    if (okayPromoteKnight != null) {
                        valid.add(okayPromoteKnight);
                    }
                    if (okayPromoteRook != null) {
                        valid.add(okayPromoteRook);
                    }
                } else {
                    ChessMove okayAttack = checkMove(board, myPosition, 1, -1, null);
                    if (okayAttack != null) {
                        valid.add(okayAttack);
                    }
                }
            }
        }
        if (((myPosition.getColumn() + 1) <= 8) && (myPosition.getRow() + 1) <= 8) {
            ChessPosition attackRight = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
            if (board.getPiece(attackRight) != null && board.getPiece(attackRight).pieceColor == ChessGame.TeamColor.BLACK) {
                if (attackRight.getRow() == 8) {
                    ChessMove okayPromoteQueen = checkMove(board, myPosition, 1, 1, PieceType.QUEEN);
                    ChessMove okayPromoteBishop = checkMove(board, myPosition, 1, 1, PieceType.BISHOP);
                    ChessMove okayPromoteKnight = checkMove(board, myPosition, 1, 1, PieceType.KNIGHT);
                    ChessMove okayPromoteRook = checkMove(board, myPosition, 1, 1, PieceType.ROOK);
                    if (okayPromoteQueen != null) {
                        valid.add(okayPromoteQueen);
                    }
                    if (okayPromoteBishop != null) {
                        valid.add(okayPromoteBishop);
                    }
                    if (okayPromoteKnight != null) {
                        valid.add(okayPromoteKnight);
                    }
                    if (okayPromoteRook != null) {
                        valid.add(okayPromoteRook);
                    }
                } else {
                    ChessMove okayAttack = checkMove(board, myPosition, 1, 1, null);
                    if (okayAttack != null) {
                        valid.add(okayAttack);
                    }
                }
            }
        }
        if ((myPosition.getRow() + 1) == 8) {
            ChessMove okayPromoteQueen = checkMove(board, myPosition, 1, 0, PieceType.QUEEN);
            ChessMove okayPromoteBishop = checkMove(board, myPosition, 1, 0, PieceType.BISHOP);
            ChessMove okayPromoteKnight = checkMove(board, myPosition, 1, 0, PieceType.KNIGHT);
            ChessMove okayPromoteRook = checkMove(board, myPosition, 1, 0, PieceType.ROOK);
            if (okayPromoteQueen != null) {
                valid.add(okayPromoteQueen);
            }
            if (okayPromoteBishop != null) {
                valid.add(okayPromoteBishop);
            }
            if (okayPromoteKnight != null) {
                valid.add(okayPromoteKnight);
            }
            if (okayPromoteRook != null) {
                valid.add(okayPromoteRook);
            }
        }
        if ((myPosition.getColumn() > 0 && myPosition.getColumn() <= 8) && ((myPosition.getRow() + 1) <= 7)) {
            ChessMove okayMove = checkMove(board, myPosition, 1, 0, null);
            if (okayMove != null) {
                valid.add(okayMove);
            }
        }

    }

    public void validKingMoves(ChessBoard board, ChessPosition myPosition, int vertical, int horizontal, Collection<ChessMove> valid) {
        if ((vertical == 1 && horizontal == 1) | (vertical == 1 && horizontal == 0) |
                (vertical == 1 && horizontal == -1) | (vertical == -1 && horizontal == -1) |
                (vertical == -1 && horizontal == 0) | (vertical == -1 && horizontal == 1) |
                (vertical == 0 && horizontal == 1) | (vertical == 1 && horizontal == 0) |
                (vertical == 0 && horizontal == -1) | (vertical == -1 && horizontal == 0)) {
            if ((myPosition.getColumn() + horizontal) > 0 && (myPosition.getColumn() + horizontal) <= 8 &&
                    (myPosition.getRow() + vertical) > 0 && (myPosition.getRow() + vertical) <= 8) {
                ChessMove okayMove = checkMove(board, myPosition, vertical, horizontal, null);
                if (okayMove != null) {
                    valid.add(okayMove);
                }
            }
        }
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<ChessMove>();
        switch (getPieceType()) {
            case KING:
                validKingMoves(board, myPosition, 1, 1, validMoves);
                validKingMoves(board, myPosition, 1, 0, validMoves);
                validKingMoves(board, myPosition, 1, -1, validMoves);
                validKingMoves(board, myPosition, -1, 1, validMoves);
                validKingMoves(board, myPosition, -1, 0, validMoves);
                validKingMoves(board, myPosition, -1, -1, validMoves);
                validKingMoves(board, myPosition, 0, 1, validMoves);
                validKingMoves(board, myPosition, 1, 0, validMoves);
                validKingMoves(board, myPosition, 0, -1, validMoves);
                validKingMoves(board, myPosition, -1, 1, validMoves);
                break;

            case QUEEN:
                validPieceMove(board, myPosition, 1, 1, null, validMoves);
                validPieceMove(board, myPosition, -1, 1, null, validMoves);
                validPieceMove(board, myPosition, 1, -1, null, validMoves);
                validPieceMove(board, myPosition, -1, -1, null, validMoves);
                validPieceMove(board, myPosition, 1, 0, null, validMoves);
                validPieceMove(board, myPosition, -1, 0, null, validMoves);
                validPieceMove(board, myPosition, 0, 1, null, validMoves);
                validPieceMove(board, myPosition, 0, -1, null, validMoves);
                break;
            case BISHOP:
                validPieceMove(board, myPosition, 1, 1, null, validMoves);
                validPieceMove(board, myPosition, -1, 1, null, validMoves);
                validPieceMove(board, myPosition, 1, -1, null, validMoves);
                validPieceMove(board, myPosition, -1, -1, null, validMoves);
                break;
            case KNIGHT:
                validKnightMove(board, myPosition, 2, 1, null, validMoves);
                validKnightMove(board, myPosition, -2, 1, null, validMoves);
                validKnightMove(board, myPosition, 2, -1, null, validMoves);
                validKnightMove(board, myPosition, -2, -1, null, validMoves);
                validKnightMove(board, myPosition, 1, 2, null, validMoves);
                validKnightMove(board, myPosition, 1, -2, null, validMoves);
                validKnightMove(board, myPosition, -1, 2, null, validMoves);
                validKnightMove(board, myPosition, -1, -2, null, validMoves);
                break;
            case ROOK:
                validPieceMove(board, myPosition, 1, 0, null, validMoves);
                validPieceMove(board, myPosition, -1, 0, null, validMoves);
                validPieceMove(board, myPosition, 0, 1, null, validMoves);
                validPieceMove(board, myPosition, 0, -1, null, validMoves);
                break;
            case PAWN:
                if (board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE) {
                    validPawnMoveWhite(board, myPosition, validMoves);
                } else {
                    validPawnMoveBlack(board, myPosition, validMoves);
                }
                break;
        }
        return validMoves;
    }
}
