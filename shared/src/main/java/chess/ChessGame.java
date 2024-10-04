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
            TeamColor color = newBoard.getPiece(startPosition).getTeamColor();
            newBoard.addPiece(validMove.getEndPosition(), newBoard.getPiece(validMove.getStartPosition()));
            newBoard.addPiece(validMove.getStartPosition(), null);
            if (!isInCheck(color, newBoard)){
                validMoveList.add(validMove);
            }
        }
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
                ChessPosition possiblePosition = new ChessPosition(row, col);
                ChessPiece pieceAt = board.getPiece(possiblePosition);
                if (pieceAt != null){
                    if (pieceAt.getPieceType() == ChessPiece.PieceType.KING &&
                            pieceAt.getTeamColor() == teamColor) {
                        return possiblePosition;
                    }
                }
            }
        }
        return null;
    }


    public Collection<ChessPosition> findEnemyPositions(TeamColor teamColor, ChessBoard board){
        Collection<ChessPosition> enemyPositions = new ArrayList<>();
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                if (board.getPiece(new ChessPosition(row, col)) != null &&
                        teamColor != board.getPiece(new ChessPosition(row, col)).getTeamColor()){
                    enemyPositions.add(new ChessPosition(row, col));
                }
            }
        }
        return enemyPositions;
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
        if (king != null) {
            Collection<ChessPosition> enemyPositions = findEnemyPositions(teamColor, board);
            for (ChessPosition enemy : enemyPositions) {
                Collection<ChessMove> possibleMoves = board.getPiece(enemy).pieceMoves(board, enemy);
                for (ChessMove move : possibleMoves) {
                    if (king.equals(move.getEndPosition())) {
                        return true;
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
        TeamColor oppositeTeam = teamColor;
        if (oppositeTeam == TeamColor.WHITE){
            oppositeTeam = TeamColor.BLACK;
        }
        else{
            oppositeTeam = TeamColor.WHITE;
        }
        Collection<ChessPosition> teamPositions = findEnemyPositions(oppositeTeam, chessBoard);
        Collection<ChessMove> allValidMoves = new ArrayList<>();
        for (ChessPosition teamMember : teamPositions) {
            Collection<ChessMove> validMoveList = validMoves(teamMember);
            allValidMoves.addAll(validMoveList);
        }
        if ((allValidMoves.isEmpty()) && (isInCheck(teamColor))) {
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
        TeamColor oppositeTeam = teamColor;
        if (oppositeTeam == TeamColor.WHITE){
            oppositeTeam = TeamColor.BLACK;
        }
        else{
            oppositeTeam = TeamColor.WHITE;
        }
        Collection<ChessPosition> teamPositions = findEnemyPositions(oppositeTeam, chessBoard);
        Collection<ChessMove> allValidMoves = new ArrayList<>();
        for (ChessPosition teamMember : teamPositions) {
            Collection<ChessMove> validMoveList = validMoves(teamMember);
            allValidMoves.addAll(validMoveList);
        }
        if ((allValidMoves.isEmpty()) && (!isInCheck(teamColor))) {
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
