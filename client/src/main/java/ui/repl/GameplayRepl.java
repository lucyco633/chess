package ui.repl;

import chess.ChessGame;
import com.google.gson.Gson;
import server.*;
import ui.ChessBoard;
import ui.GameplayClient;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.ROOK_CHARACTER;

public class GameplayRepl implements ErrorMessageHandler, LoadGameMessageHandler, NotificationMessageHandler,
        ServerMessageHandler {
    private final GameplayClient client;

    public GameplayRepl(String url, String authToken, int gameId, String team) throws ResponseException {
        client = new GameplayClient(url, authToken, gameId, this,
                this, this, this);
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
                if (command.equals("leave")) {
                    return;
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        System.out.print("\nWhat do you want to do? >>> \n");
    }

    @Override
    public void errorNotify(ErrorMessage errorMessage) {
        System.out.print(errorMessage.getErrorMessage());
    }

    @Override
    public void loadGame(LoadGameMessage loadGameMessage) {
        String chessGameString = loadGameMessage.getGame();
        ChessGame chessGameRecieved = new Gson().fromJson(chessGameString, ChessGame.class);
        chess.ChessBoard chessBoardReal = chessGameRecieved.getBoard();
        ChessBoard chessBoard = new ChessBoard();
        GameplayClient.chessGame = chessGameRecieved;
        //add to print correct board for black too
        if (Objects.equals(GameplayClient.team, "BLACK")) {
            chessBoard.printChessBoard(System.out, chessBoardReal, false, chessGameRecieved, null);
        } else {
            chessBoard.printReversedChessBoard(System.out, chessBoardReal, false, chessGameRecieved, null);
        }
    }

    @Override
    public void notify(NotificationMessage notificationMessage) {
        System.out.print(notificationMessage.getMessage());
    }

    @Override
    public void notify(ServerMessage serverMessage) {
    }
}
