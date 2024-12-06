package server;

import websocket.messages.ErrorMessage;

public interface ErrorMessageHandler {
    void errorNotify(ErrorMessage errorMessage);
}
