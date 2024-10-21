package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    //map with gameID as the key and GameData object as the value
    HashMap<Integer, GameData> gameDB = new HashMap<>();

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
    public void createGame(String gameName) throws DataAccessException{
        //how to set usernames and gameID??
        GameData newGame = new GameData(1234, "whiteUsername", "blackUsername", gameName, new ChessGame());
        gameDB.put(newGame.gameID(), newGame);
    }

    @Override
    public void delete() throws DataAccessException{
        gameDB.clear();
    }
}
