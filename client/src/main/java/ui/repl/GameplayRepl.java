package ui.repl;

import chess.ChessGame;
import com.google.gson.Gson;
import server.*;
import ui.ChessBoard;
import ui.GameplayClient;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

import static ui.EscapeSequences.ROOK_CHARACTER;

public class GameplayRepl implements ErrorMessageHandler, LoadGameMessageHandler, NotificationMessageHandler {
    private final GameplayClient client;

    public GameplayRepl(String url, String authToken, int gameId,
                        ChessGame chessGame, String team) throws ResponseException {
        client = new GameplayClient(url, authToken, gameId, chessGame, team);
    }

    public void run() {
        System.out.println(ROOK_CHARACTER + "Your chess game. Select a command to start." + ROOK_CHARACTER);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {
            printPrompt();
            String line = scanner.nextLine();
            String lineCommandArray[] = line.split(" ", 2);
            String command = lineCommandArray[0];

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        System.out.print("\n" + "What do you want to do?" + ">>> ");
    }

    @Override
    public void errorNotify(ErrorMessage errorMessage) {
        System.out.print(errorMessage.getErrorMessage());
    }

    @Override
    public void loadGame(LoadGameMessage loadGameMessage) {
        String chessGameString = loadGameMessage.getGame();
        ChessGame chessGame = new Gson().fromJson(chessGameString, ChessGame.class);
        chess.ChessBoard chessBoardReal = chessGame.getBoard();
        ChessBoard chessBoard = new ChessBoard();
        //add to print correct board for black too
        chessBoard.printChessBoard(System.out, chessBoardReal, false, chessGame, null);
    }

    @Override
    public void notify(NotificationMessage notificationMessage) {
        System.out.print(notificationMessage.getMessage());
    }
}
