package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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

    public void checkMove(ChessBoard board, ChessPosition myPosition, int vertical, int horizontal, PieceType promotion, Collection<ChessMove> valid) {
        if (((myPosition.getRow() + vertical) < 8) && ((myPosition.getRow() + vertical) >= 0) &&
                ((myPosition.getColumn() + horizontal) < 8) && ((myPosition.getColumn() + horizontal) >= 0)){
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + vertical, myPosition.getColumn() + horizontal);
            if (board.getPiece(newPosition) == null) {
                valid.add(new ChessMove(myPosition, newPosition, promotion));
            }
            else if ((board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.BLACK && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                    | (board.getPiece(newPosition).pieceColor == ChessGame.TeamColor.WHITE && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)){
                valid.add(new ChessMove(myPosition, newPosition, promotion));
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
        switch (type) {
            case KING:
                //add while statement so that stop before 8?
                ArrayList<Integer> horiz = new ArrayList<Integer>();
                horiz.add(-1);
                horiz.add(0);
                horiz.add(1);
                ArrayList<Integer> vert = new ArrayList<Integer>();
                vert.add(-1);
                vert.add(0);
                vert.add(1);
                for (Integer v: vert){
                    for (Integer h: horiz){
                        checkMove(board, myPosition, v, h, null, validMoves);
                    }
                }
            case QUEEN:
                break;
            case BISHOP:
                break;
            case KNIGHT:
                break;
            case ROOK:
                //while the next spot to the side is null...
                //if next spot has enemy player, can move, else not
                //same for up and down
                int moveRookLeft = 0;
                while ((myPosition.getColumn() + moveRookLeft) >= 0){
                    checkMove(board, myPosition, moveRookLeft, 0, null, validMoves);
                    moveRookLeft--;
                }
                int moveRookRight = 0;
                while ((myPosition.getColumn() + moveRookRight) < 8){
                    checkMove(board, myPosition, moveRookRight, 0, null, validMoves);
                    moveRookRight++;
                }
                int moveRookDown = 0;
                while ((myPosition.getRow() + moveRookDown) >= 0){
                    checkMove(board, myPosition, 0, moveRookDown, null, validMoves);
                    moveRookDown--;
                }
                int moveRookUp = 0;
                while ((myPosition.getRow() + moveRookUp) < 8){
                    checkMove(board, myPosition, 0, moveRookUp, null, validMoves);
                    moveRookUp++;
                }
            case PAWN:
                break;
        }
        return validMoves;
    }
}
