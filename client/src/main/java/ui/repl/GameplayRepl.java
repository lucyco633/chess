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

public class GameplayRepl {
    private final GameplayClient client;

    public GameplayRepl(String url, String authToken, int gameId, String team) throws ResponseException {
        client = new GameplayClient(url, authToken, gameId);
    }

    public void run() {
        System.out.println(ROOK_CHARACTER + "Your chess game. Select a command to start.\n" + ROOK_CHARACTER);
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

}
