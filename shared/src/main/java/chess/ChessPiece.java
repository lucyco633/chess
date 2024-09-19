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
                if ((myPosition.getRow() + 1) < 8){
                    ChessPosition topMid = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                    if (board.getPiece(topMid) == null) {
                        validMoves.add(new ChessMove(myPosition, topMid, type));
                    }
                    else if ((board.getPiece(topMid).pieceColor == ChessGame.TeamColor.BLACK && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                            | (board.getPiece(topMid).pieceColor == ChessGame.TeamColor.WHITE && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)){
                        validMoves.add(new ChessMove(myPosition, topMid, type));
                    }
                }
                if (((myPosition.getColumn() + 1) < 8) && ((myPosition.getRow() + 1) < 8)) {
                    ChessPosition topRight = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
                    if (board.getPiece(topRight) == null) {
                        validMoves.add(new ChessMove(myPosition, topRight, type));
                    }
                    else if ((board.getPiece(topRight).pieceColor == ChessGame.TeamColor.BLACK && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                            | (board.getPiece(topRight).pieceColor == ChessGame.TeamColor.WHITE && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)){
                        validMoves.add(new ChessMove(myPosition, topRight, type));
                    }
                }
                if ((myPosition.getColumn() + 1) < 8) {
                    ChessPosition right = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
                    if (board.getPiece(right) == null) {
                        validMoves.add(new ChessMove(myPosition, right, type));
                    }
                    else if ((board.getPiece(right).pieceColor == ChessGame.TeamColor.BLACK && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                            | (board.getPiece(right).pieceColor == ChessGame.TeamColor.WHITE && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)){
                        validMoves.add(new ChessMove(myPosition, right, type));
                    }
                }
                if (((myPosition.getColumn() + 1) < 8) && ((myPosition.getRow() - 1) >= 0)) {
                    ChessPosition downRight = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                    if (board.getPiece(downRight) == null) {
                        validMoves.add(new ChessMove(myPosition, downRight, type));
                    }
                    else if ((board.getPiece(downRight).pieceColor == ChessGame.TeamColor.BLACK && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                            | (board.getPiece(downRight).pieceColor == ChessGame.TeamColor.WHITE && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)){
                        validMoves.add(new ChessMove(myPosition, downRight, type));
                    }
                }
                if ((myPosition.getRow() - 1) >= 0){
                    ChessPosition downMid = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
                    if (board.getPiece(downMid) == null){
                        validMoves.add(new ChessMove(myPosition, downMid, type));
                    }
                    else if ((board.getPiece(downMid).pieceColor == ChessGame.TeamColor.BLACK && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                            | (board.getPiece(downMid).pieceColor == ChessGame.TeamColor.WHITE && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)){
                        validMoves.add(new ChessMove(myPosition, downMid, type));
                    }
                }
                if (((myPosition.getColumn() - 1) >= 0) && ((myPosition.getRow() - 1) >= 0)) {
                    ChessPosition downLeft = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                    if (board.getPiece(downLeft) == null) {
                        validMoves.add(new ChessMove(myPosition, downLeft, type));
                    }
                    else if ((board.getPiece(downLeft).pieceColor == ChessGame.TeamColor.BLACK && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                            | (board.getPiece(downLeft).pieceColor == ChessGame.TeamColor.WHITE && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)){
                        validMoves.add(new ChessMove(myPosition, downLeft, type));
                    }
                }
                if ((myPosition.getColumn() - 1) >= 0) {
                    ChessPosition left = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
                    if (board.getPiece(left) == null) {
                        validMoves.add(new ChessMove(myPosition, left, type));
                    }
                    else if ((board.getPiece(left).pieceColor == ChessGame.TeamColor.BLACK && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                            | (board.getPiece(left).pieceColor == ChessGame.TeamColor.WHITE && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)){
                        validMoves.add(new ChessMove(myPosition, left, type));
                    }
                }
                if (((myPosition.getColumn() - 1) >= 0) && ((myPosition.getRow() + 1) < 8)) {
                    ChessPosition topLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
                    if (board.getPiece(topLeft) == null) {
                        validMoves.add(new ChessMove(myPosition, topLeft, type));
                    }
                    else if ((board.getPiece(topLeft).pieceColor == ChessGame.TeamColor.BLACK && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE)
                            | (board.getPiece(topLeft).pieceColor == ChessGame.TeamColor.WHITE && board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.BLACK)){
                        validMoves.add(new ChessMove(myPosition, topLeft, type));
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
