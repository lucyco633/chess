package server;

import websocket.messages.LoadGameMessage;

public interface LoadGameMessageHandler {
    void loadGame(LoadGameMessage loadGameMessage);
}
