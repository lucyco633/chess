package dataaccess;
import chess.ChessGame;
import model.GameData;


public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException;

    void createGame(String gameName) throws DataAccessException;

    void delete() throws DataAccessException;
}
