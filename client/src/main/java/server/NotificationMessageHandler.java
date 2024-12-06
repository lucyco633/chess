package server;

import websocket.messages.NotificationMessage;

public interface NotificationMessageHandler {
    void notify(NotificationMessage notificationMessage);
}
