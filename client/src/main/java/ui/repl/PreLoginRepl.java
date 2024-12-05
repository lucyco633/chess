package ui.repl;

import server.ResponseException;
import ui.PreLoginClient;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginRepl {
    private final PreLoginClient client;

    public PreLoginRepl(String serverUrl) throws ResponseException {
        client = new PreLoginClient(serverUrl);
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

}
