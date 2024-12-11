package server;

import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {
    Session session;

    public WebSocketCommunicator(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case ERROR:
                            ErrorMessageHandler errorMessageHandler = new ErrorMessageHandler();
                            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                            errorMessageHandler.errorNotify(errorMessage);
                            break;
                        case NOTIFICATION:
                            NotificationMessageHandler notificationMessageHandler = new NotificationMessageHandler();
                            NotificationMessage notificationMessage = new Gson().fromJson(message,
                                    NotificationMessage.class);
                            notificationMessageHandler.notify(notificationMessage);
                            break;
                        case LOAD_GAME:
                            LoadGameMessageHandler loadGameMessageHandler = new LoadGameMessageHandler();
                            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            loadGameMessageHandler.loadGame(loadGameMessage);
                            break;
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
