package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    //create private final of board?
    private TeamColor teamTurn;
    private ChessBoard chessBoard;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     * keep track of turns? Always starts with white/or black, then switch off from there?
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     * call validMoves from chessMove?
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (chessBoard.getPiece(startPosition).getPieceType() == null){
            return null;
        }
        ChessPiece chessPiece = new ChessPiece(teamTurn, chessBoard.getPiece(startPosition).getPieceType());
        Collection<ChessMove> validMoveList = chessPiece.pieceMoves(chessBoard, startPosition);
        return validMoveList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     * change row and col of piece
     * SWITCH TURNS
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoveList = validMoves(move.getStartPosition());
        if (!validMoveList.contains(move)){
            //throw InvalidMoveException("Invalid move");
        }
        else {
            if (move.getPromotionPiece() != null){
                //set type to promotion piece
                ChessPiece promotedPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
                chessBoard.addPiece(move.getEndPosition(), promotedPiece);
                chessBoard.addPiece(move.getStartPosition(), null);
                if (teamTurn == TeamColor.WHITE){
                    teamTurn = TeamColor.BLACK;
                }
                if (teamTurn == TeamColor.BLACK){
                    teamTurn = TeamColor.WHITE;
                }
            }
            ChessPiece piece = chessBoard.getPiece(move.getStartPosition());
            chessBoard.addPiece(move.getEndPosition(), piece);
            chessBoard.addPiece(move.getStartPosition(), null);
            if (teamTurn == TeamColor.WHITE){
                teamTurn = TeamColor.BLACK;
            }
            if (teamTurn == TeamColor.BLACK){
                teamTurn = TeamColor.WHITE;
            }
        }
    }

    private ChessPosition kingPosition(TeamColor teamColor){
        ChessPiece king = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                if (chessBoard.getPiece(new ChessPosition(row, col)) == king){
                    return new ChessPosition(row, col);
                }
            }
        }
        return null;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = kingPosition(teamColor);
        if (teamColor == TeamColor.WHITE){
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition enemyPosition = new ChessPosition(row, col);
                    if (chessBoard.getPiece(enemyPosition).getTeamColor() == TeamColor.BLACK) {
                        Collection<ChessMove> validMoveList = validMoves(enemyPosition);
                        for (ChessMove pieceMove : validMoveList) {
                            if (king.getRow() == pieceMove.getEndPosition().getRow() &&
                                    king.getColumn() == pieceMove.getEndPosition().getColumn()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        if (teamColor == TeamColor.BLACK){
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition enemyPosition = new ChessPosition(row, col);
                    if (chessBoard.getPiece(enemyPosition).getTeamColor() == TeamColor.WHITE) {
                        Collection<ChessMove> validMoveList = validMoves(enemyPosition);
                        for (ChessMove pieceMove : validMoveList) {
                            if (king.getRow() == pieceMove.getEndPosition().getRow() &&
                                    king.getColumn() == pieceMove.getEndPosition().getColumn()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     *              use setBoard from ChessBoard?
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
