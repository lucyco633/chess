package server.websocket;

import com.google.gson.Gson;
import dataaccess.SqlGameDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.util.ArrayList;
import java.util.Map;

@WebSocket
public class WebSocketServer {

    //create complex data structure that will hold onto game, clients, and session
    //add functionality to receive user commands and send server messages
    public Map<Integer, Map<String, ArrayList<String>>> websocketStructure;
    public static SqlGameDAO sqlGameDAO;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {

        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        MakeMoveCommand makeMoveCommand;

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
        }
    }

    public void makeMove(MakeMoveCommand makeMoveCommand) {
        Map<String, ArrayList<String>> clients = websocketStructure.get(makeMoveCommand.getGameID());
        ArrayList<String> players = clients.get("players");
        ArrayList<String> observers = clients.get("observers");

    }

}
