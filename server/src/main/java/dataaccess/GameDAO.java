package dataaccess;
import chess.ChessGame;
import model.GameData;


public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;
}
