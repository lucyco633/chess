package server;

import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage serverMessage);
}

