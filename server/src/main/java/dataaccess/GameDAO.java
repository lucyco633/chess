package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.ResultExceptions;


public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException, ResultExceptions;

    void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)
            throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException, ResultExceptions;

}
