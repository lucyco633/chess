package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Random;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    //map with gameID as the key and GameData object as the value
    public static HashMap<Integer, GameData> gameDB = new HashMap<>();

    @Override
    public GameData getGame(int gameID) {
        return gameDB.get(gameID);
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, boolean resigned) {
        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game, resigned);
        gameDB.put(gameID, updatedGame);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        Random rand = new Random();
        GameData newGame = new GameData(rand.nextInt(100), null, null, gameName, new ChessGame(), false);
        gameDB.put(newGame.gameID(), newGame);
        return newGame;
    }
}
