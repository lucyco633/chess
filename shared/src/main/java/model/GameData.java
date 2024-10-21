package model;

import chess.ChessGame;
import java.util.Map;
import java.util.HashMap;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public static Map<Integer, GameData> gamesList = new HashMap<>();

    public GameData setGameID(int gameID){
        return new GameData(gameID, this.whiteUsername, this.blackUsername, this.gameName, this.game);
    }

    public GameData setWhiteUsername(String whiteUsername){
        return new GameData(this.gameID, whiteUsername, this.blackUsername, this.gameName, this.game);
    }

    public GameData setBlackUsername(String blackUsername){
        return new GameData(this.gameID, this.whiteUsername, blackUsername, this.gameName, this.game);
    }

    public GameData setGameName(String gameName){
        return new GameData(this.gameID, this.whiteUsername, this.blackUsername, gameName, this.game);
    }

    public GameData createGame(String gameName){
        //create game with arbitrary parameters? random id?
        return null;
    }

    public GameData addGame(GameData game){
        gamesList.put(game.gameID, game);
        return game;
    }

    public GameData getGame(int gameID){
        if (gamesList.containsKey(gameID)){
            return gamesList.get(gameID);
        }
        return null;
    }

    public Map<Integer, GameData> listGames(){
        return gamesList;
    }

    public GameData deleteGameData(){
        gamesList.clear();
        return null;
    }
}
