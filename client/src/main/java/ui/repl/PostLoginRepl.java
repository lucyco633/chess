package ui.repl;

import chess.ChessGame;
import com.google.gson.Gson;
import server.*;
import ui.ChessBoard;
import ui.PostLoginClient;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.ROOK_CHARACTER;

public class PostLoginRepl implements LoadGameMessageHandler, NotificationMessageHandler, ErrorMessageHandler,
        ServerMessageHandler {
    private final PostLoginClient client;
    public ChessGame chessGame;

    public PostLoginRepl(String serverUrl, String userAuthorization) throws ResponseException {
        client = new PostLoginClient(serverUrl, userAuthorization, this,
                this, this, this);
    }

    public void run() {
        System.out.println(ROOK_CHARACTER + "Your chess homepage. Select a command to start." + ROOK_CHARACTER);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            printPrompt();
            String line = scanner.nextLine();
            String lineCommandArray[] = line.split(" ", 2);
            String command = lineCommandArray[0];

            try {
                result = client.eval(line, chessGame);
                System.out.print(result);
                if (command.equals("logout")) {
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
        System.out.print("\n" + "What do you want to do?" + ">>> ");
    }

    @Override
    public void errorNotify(ErrorMessage errorMessage) {
        System.out.print(errorMessage.getErrorMessage());
    }

    @Override
    public void loadGame(LoadGameMessage loadGameMessage) {
        String chessGameString = loadGameMessage.getGame();
        ChessGame chessGameReceived = new Gson().fromJson(chessGameString, ChessGame.class);
        chess.ChessBoard chessBoardReal = chessGameReceived.getBoard();
        ChessBoard chessBoard = new ChessBoard();
        chessGame = chessGameReceived;
        //add to print correct board for black too
        chessBoard.printChessBoard(System.out, chessBoardReal, false, chessGameReceived, null);
    }

    @Override
    public void notify(NotificationMessage notificationMessage) {
        System.out.print(notificationMessage.getMessage());
    }

    @Override
    public void notify(ServerMessage serverMessage) {
    }
}
