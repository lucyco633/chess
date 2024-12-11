package server;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.ChessBoard;
import ui.GameplayClient;
import websocket.messages.LoadGameMessage;

import java.util.Objects;

public class LoadGameMessageHandler {
    void loadGame(LoadGameMessage loadGameMessage) {
        String chessGameString = loadGameMessage.getGame();
        ChessGame chessGameRecieved = new Gson().fromJson(chessGameString, ChessGame.class);
        chess.ChessBoard chessBoardReal = chessGameRecieved.getBoard();
        GameplayClient.chessGame = chessGameRecieved;
        if (Objects.equals(GameplayClient.team, "BLACK")) {
            ChessBoard.printChessBoard(System.out, chessBoardReal, false,
                    chessGameRecieved, null);
        } else {
            ChessBoard.printReversedChessBoard(System.out, chessBoardReal, false,
                    chessGameRecieved, null);
        }
    }
}
