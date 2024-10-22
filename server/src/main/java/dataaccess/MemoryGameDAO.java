package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    //map with gameID as the key and GameData object as the value
    public HashMap<Integer, GameData> gameDB = new HashMap<>();

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        return gameDB.get(gameID);
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {
        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        gameDB.put(gameID, updatedGame);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException{
        //how to set usernames and gameID?? random or sequentially
        GameData newGame = new GameData(1234, null, null, gameName, new ChessGame());
        gameDB.put(newGame.gameID(), newGame);
        return newGame;
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException{
        gameDB.remove(gameID);
    }
}
