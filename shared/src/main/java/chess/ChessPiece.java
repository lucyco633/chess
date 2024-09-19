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
                if ((myPosition.getRow() + 1) < 8) {
                    //null or other team
                    if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), type));
                    }
                }
                if (((myPosition.getColumn() + 1) < 8) && ((myPosition.getRow() + 1) < 8)) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), type));
                    }
                }
                if ((myPosition.getColumn() + 1) < 8) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1)) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), type));
                    }
                }
                if (((myPosition.getColumn() + 1) < 8) && ((myPosition.getRow() - 1) >= 0)) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), type));
                    }
                }
                if ((myPosition.getRow() - 1) >= 0){
                    if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null){
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), type));
                    }
                }
                if (((myPosition.getColumn() - 1) >= 0) && ((myPosition.getRow() - 1) >= 0)) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), type));
                    }
                }
                if ((myPosition.getColumn() - 1) >= 0) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1)) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1), type));
                    }
                }
                if (((myPosition.getColumn() - 1) >= 0) && ((myPosition.getRow() + 1) < 8)) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) == null) {
                        validMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), type));
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
                break;
            case PAWN:
                break;
        }
        return validMoves;
    }
}
