package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
        Collection<ChessMove> valid_moves = new Collection<ChessMove>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<ChessMove> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean add(ChessMove chessMove) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends ChessMove> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };
        switch (type) {
            case KING:
                if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null){
                    valid_moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), type));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) == null){
                    valid_moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), type));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1)) == null){
                    valid_moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), type));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) == null){
                    valid_moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), type));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null){
                    valid_moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), type));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) == null){
                    valid_moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), type));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1)) == null){
                    valid_moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1), type));
                }
                if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) == null){
                    valid_moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), type));
                }
            case QUEEN:
                break;
            case BISHOP:
                break;
            case KNIGHT:
                break;
            case ROOK:
                break;
            case PAWN:
                break;
        }
        return valid_moves;
    }
}
