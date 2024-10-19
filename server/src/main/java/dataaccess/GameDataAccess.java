package dataaccess;
import dataaccess.DataAccessException;
import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.Map;

public interface GameDataAccess {
    GameData getGame(int gameID) throws DataAccessException;

    Map<Integer, GameData> listGames(int gameID) throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;
}
