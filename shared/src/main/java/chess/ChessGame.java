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
    private ChessBoard chessBoard = new ChessBoard();
    public ChessGame() {
        chessBoard.resetBoard();
        teamTurn = TeamColor.WHITE;
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
        if (chessBoard.getPiece(startPosition) == null){
            return new ArrayList<>();
        }
        Collection<ChessMove> possibleMoveList = chessBoard.getPiece(startPosition).pieceMoves(chessBoard, startPosition);
        Collection<ChessMove> validMoveList = new ArrayList<>();
        for (ChessMove validMove: possibleMoveList){
            ChessBoard newBoard = chessBoard.clone();
            newBoard.addPiece(validMove.getEndPosition(), newBoard.getPiece(validMove.getStartPosition()));
            newBoard.addPiece(validMove.getStartPosition(), null);
            //how to make sure I'm running isInCheck on cloned board?
            if (!isInCheck(teamTurn, newBoard)){
                validMoveList.add(validMove);
            }
        }
        return validMoveList;
        //use isInCheck to remove from list
        //clone to test moves to see if it puts me in check
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
            throw new InvalidMoveException("Move not in valid move list");
        }
        if (teamTurn != chessBoard.getPiece(move.getStartPosition()).getTeamColor()){
            throw new InvalidMoveException("Wrong team move");
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
                else {
                    teamTurn = TeamColor.WHITE;
                }
            }
            else {
                ChessPiece piece = chessBoard.getPiece(move.getStartPosition());
                chessBoard.addPiece(move.getEndPosition(), piece);
                chessBoard.addPiece(move.getStartPosition(), null);
                if (teamTurn == TeamColor.WHITE) {
                    teamTurn = TeamColor.BLACK;
                }
                else {
                    teamTurn = TeamColor.WHITE;
                }
            }
        }
    }

    private ChessPosition kingPosition(TeamColor teamColor, ChessBoard board){
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                if (board.getPiece(new ChessPosition(row, col)) != null){
                    if (board.getPiece(new ChessPosition(row, col)).getPieceType() == ChessPiece.PieceType.KING &&
                            board.getPiece(new ChessPosition(row, col)).getTeamColor() == teamColor) {
                        return new ChessPosition(row, col);
                    }
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
    public boolean isInCheck(TeamColor teamColor){
        return isInCheck(teamColor, chessBoard);
    }


    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition king = kingPosition(teamColor, board);
        if (king != null){
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition enemyPosition = new ChessPosition(row, col);
                    if (board.getPiece(enemyPosition) != null){
                        if (board.getPiece(enemyPosition).getTeamColor() != teamColor) {
                            Collection<ChessMove> validMoveList = board.getPiece(enemyPosition).pieceMoves(board, enemyPosition);
                            for (ChessMove pieceMove : validMoveList) {
                                if (king.equals(pieceMove.getEndPosition())) {
                                    return true;
                                }
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
        ChessPosition king = kingPosition(teamColor, chessBoard);
        Collection<ChessMove> validMoveList = validMoves(king);
        if ((validMoveList.isEmpty()) && (isInCheck(teamColor))){
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition king = kingPosition(teamColor, chessBoard);
        Collection<ChessMove> validMoveList = validMoves(king);
        if ((validMoveList.isEmpty()) && (!isInCheck(teamColor))){
            return true;
        }
        return false;
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
