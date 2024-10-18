package model;

import chess.ChessGame;

public record Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public Game setGameID(int gameID){
        return new Game(gameID, this.whiteUsername, this.blackUsername, this.gameName, this.game);
    }

    public Game setWhiteUsername(String whiteUsername){
        return new Game(this.gameID, whiteUsername, this.blackUsername, this.gameName, this.game);
    }

    public Game setBlackUsername(String blackUsername){
        return new Game(this.gameID, this.whiteUsername, blackUsername, this.gameName, this.game);
    }

    public Game setGameName(String gameName){
        return new Game(this.gameID, this.whiteUsername, this.blackUsername, gameName, this.game);
    }
}
