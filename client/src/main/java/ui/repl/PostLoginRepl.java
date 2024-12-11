package ui.repl;

import server.*;
import ui.PostLoginClient;

import java.util.Scanner;

import static ui.EscapeSequences.ROOK_CHARACTER;

public class PostLoginRepl {
    private final PostLoginClient client;

    public PostLoginRepl(String serverUrl, String userAuthorization) throws ResponseException {
        client = new PostLoginClient(serverUrl, userAuthorization);
    }

    public void run() {
        System.out.println(ROOK_CHARACTER + "Your chess homepage. Select a command to start.\n" + ROOK_CHARACTER);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            printPrompt();
            String line = scanner.nextLine();
            String lineCommandArray[] = line.split(" ", 2);
            String command = lineCommandArray[0];

            try {
                result = client.eval(line);
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
        System.out.print("\nWhat do you want to do? >>> \n");
    }

}
