package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.ResultExceptions;

import java.sql.SQLException;


public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException, ResultExceptions;

    void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, boolean resigned)
            throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException, ResultExceptions, SQLException;

}
