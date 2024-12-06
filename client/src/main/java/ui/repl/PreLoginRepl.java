package ui.repl;

import server.*;
import ui.PreLoginClient;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginRepl implements NotificationMessageHandler, ErrorMessageHandler,
        LoadGameMessageHandler, ServerMessageHandler {
    private final PreLoginClient client;

    public PreLoginRepl(String serverUrl) throws ResponseException {
        client = new PreLoginClient(serverUrl, this, this,
                this, this);
    }

    public void run() {
        System.out.println(ROOK_CHARACTER + "Welcome to chess! Login to start." + ROOK_CHARACTER);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
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
    }

    @Override
    public void loadGame(LoadGameMessage loadGameMessage) {
    }

    @Override
    public void notify(NotificationMessage notificationMessage) {
    }

    @Override
    public void notify(ServerMessage serverMessage) {
    }
}
