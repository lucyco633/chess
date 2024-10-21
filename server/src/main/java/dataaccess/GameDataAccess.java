package dataaccess;
import model.GameData;

import java.util.Map;

public interface GameDataAccess {
    GameData getGame(int gameID) throws DataAccessException;

    Map<Integer, GameData> listGames(int gameID) throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    void deleteAllGameData() throws DataAccessException;
}
