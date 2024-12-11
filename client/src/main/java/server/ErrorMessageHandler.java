package server;

import websocket.messages.ErrorMessage;

public class ErrorMessageHandler {
    void errorNotify(ErrorMessage errorMessage) {
        System.out.print(errorMessage.getErrorMessage());
    }
}
