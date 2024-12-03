package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

@WebSocket
public class WebSocketServer {

    //create complex data structure that will hold onto game, clients, and session
    //add functionality to receive user commands and send server messages


    @OnWebSocketMessage
    public void onMessage(Session session, String message) {

        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        MakeMoveCommand makeMoveCommand;

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
        }
    }

}
