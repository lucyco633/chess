package server;

import websocket.messages.NotificationMessage;

public class NotificationMessageHandler {
    void notify(NotificationMessage notificationMessage) {
        System.out.print(notificationMessage.getMessage());
    }
}
